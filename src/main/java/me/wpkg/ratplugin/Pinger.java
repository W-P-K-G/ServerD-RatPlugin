package me.wpkg.ratplugin;

import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.listener.ExecutionController;
import com.serverd.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Pinger
{
    public double interval;
    public int timeout;

    boolean buffer = false;

    boolean isPinging = false,isRunning = false;

    public ArrayList<Integer> excludes = new ArrayList<>();

    public Pinger(Plugin plugin,double interval,int timeout)
    {
        this.timeout = timeout;
        this.interval = interval;

        plugin.addExecutionController(new Receiver(this));
    }

    public void timeoutWaiting(Plugin plugin,Client client,ArrayList<Client> forRemoval)
    {
        for (int i = 0; i < timeout;i+=100)
        {
            if (!client.isConnected())
            {
                plugin.info("Client " + client.getID() + " closed connection, ignoring...");
                buffer = false;
                return;
            }
            if (buffer)
            {
                buffer = false;

                plugin.info("Client " + client.getID() + " responsed");
                return;
            }
            Util.sleep(Math.min(100,i));
        }
        buffer = false;

        forRemoval.add(client);
    }
    public void stop()
    {
        isRunning = false;
    }

    public void startPinger(Plugin plugin,RatPlugin instance)
    {
        isRunning = true;
        while (plugin.isRunned() && isRunning)
        {
            isPinging = false;
            Util.sleep((long) (interval * 60 * 1000));

            isPinging = true;

            if (instance.ratsID.isEmpty() || (instance.ratsID.size() == excludes.size()))
                continue;

            plugin.info("Starting pinging...");
            ArrayList<Client> forRemoval = new ArrayList<>();

            ArrayList<Client> rats = new ArrayList<>();
            for (int id : instance.ratsID)
                if (!excludes.contains(id))
                    rats.add(ClientManager.getClient(id));

            for (Client client : rats)
            {
                try
                {
                    client.send("ping");
                    timeoutWaiting(plugin, client,forRemoval);
                }
                catch (IOException e)
                {
                    plugin.error("Error sending message: " + e.getMessage());
                }
            }

            Collections.reverse(forRemoval);
            for (Client client : forRemoval)
            {
                plugin.info("Client " + client.getID() + " not responsing, removing...");
                ClientManager.delete(client.getID());
            }
        }
        isRunning = false;
    }
}

class Receiver implements ExecutionController
{
    Pinger pinger;
    public Receiver(Pinger pinger)
    {
        this.pinger = pinger;
    }

    @Override
    public boolean controlCommand(String command, String[] args, Client client, Plugin plugin)
    {
        if (pinger.isPinging)
        {
            if (command.equals("ping-received"))
            {
                pinger.buffer = true;
                return false;
            }
            else return true;
        }
        else return true;
    }
}
