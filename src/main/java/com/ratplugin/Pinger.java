package com.ratplugin;

import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class Pinger
{
    public double interval;
    public int timeout;

    boolean buffer = false;

    public Pinger(Plugin plugin,double interval,int timeout)
    {
        this.timeout = timeout;
        this.interval = interval;

        plugin.addCommand(new ReceivedCommand(this,"ping-received"));
        //backward compatibility with "normal message" ping from WPKG rat
        plugin.addCommand(new ReceivedCommand(this,"Ping"));
    }

    public void timeoutWaiting(Plugin plugin,Client client,ArrayList<Client> forRemoval)
    {
        for (int i = 0; i < timeout;i+=100)
        {
            if (!client.connected)
            {
                plugin.info("Client " + client.id + " closed connection, ignoring...");
                buffer = false;
                return;
            }
            if (buffer)
            {
                buffer = false;

                plugin.info("Client " + client.id + " responsed");
                return;
            }
            Util.sleep(Math.min(100,i));
        }
        buffer = false;

        forRemoval.add(client);
    }

    public void startPinger(Plugin plugin,RatPlugin instance)
    {
        while (plugin.isRunned)
        {
            Util.sleep((long) (interval * 60 * 1000));

            if (instance.ratsID.isEmpty())
                continue;

            plugin.info("Starting pinging...");
            ArrayList<Client> forRemoval = new ArrayList<>();

            ArrayList<Client> rats = new ArrayList<>();
            for (int id : instance.ratsID)
                rats.add(ClientManager.getClient(id));

            for (Client client : rats)
            {
                client.send("ping");

                timeoutWaiting(plugin, client,forRemoval);
            }

            Collections.reverse(forRemoval);
            for (Client client : forRemoval)
            {
                plugin.info("Client " + client.id + " not responsing, removing...");
                ClientManager.delete(client.id);
            }
        }
    }
}

class ReceivedCommand extends Command
{
    Pinger pinger;
    public ReceivedCommand(Pinger pinger,String pingKeyword)
    {
        this.pinger = pinger;

        command = pingKeyword;
        //no help
        help = "";
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin)
    {
        pinger.buffer = true;
    }
}
