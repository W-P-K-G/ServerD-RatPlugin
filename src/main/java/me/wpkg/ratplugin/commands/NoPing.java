package me.wpkg.ratplugin.commands;

import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import me.wpkg.ratplugin.Pinger;

import java.io.IOException;

public class NoPing extends Command
{
    Pinger pinger;
    public NoPing(Pinger pinger)
    {
        this.pinger = pinger;

        command = "/noping";
        help = "/noping - disabling/enabling pinging current rat";
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        if (pinger.excludes.contains(client.getID()))
        {
            pinger.excludes.remove(pinger.excludes.lastIndexOf(client.getID()));
            client.send("enabled");
        }
        else
        {
            pinger.excludes.add(client.getID());
            client.send("disabled");
        }
    }
}
