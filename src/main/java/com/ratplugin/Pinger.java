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

    public void timeoutWaiting(Plugin plugin,Client client,ArrayList<Integer> forRemoval)
    {
        for (int i = 0; i < timeout;i+=100)
        {
            if (!client.connected)
            {
                plugin.Log("Client " + client.id + " closed connection, ignoring...");
                return;
            }
            if (buffer)
            {
                buffer = false;

                plugin.Log("Client " + client.id + " responsed");
                return;
            }
            Util.sleep(Math.min(100,i));
        }
        buffer = false;

        forRemoval.add(client.id);
    }

    public void startPinger(Plugin plugin,RatPlugin instance)
    {
        while (plugin.isRunned)
        {
            Util.sleep((long) (interval * 60 * 1000));

            if (instance.ratsID.isEmpty())
                continue;

            plugin.Log("Starting pinging...");
            ArrayList<Integer> forRemoval = new ArrayList<>();

            int index = 0;
            while (index < instance.ratsID.size())
            {
                Client client = ClientManager.getClient(index);

                client.send("ping");

                timeoutWaiting(plugin, client,forRemoval);
                index++;
            }

            Collections.reverse(forRemoval);
            for (int id : forRemoval)
            {
                plugin.Log("Client " + id + " not responsing, removing...");
                ClientManager.delete(id);
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
