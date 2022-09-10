package com.ratplugin.commands;

import com.ratplugin.ClientUtils;
import com.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;

import java.io.IOException;
import java.util.ArrayList;

public class About extends Command implements ConnectListener
{
    public class AboutInfo
    {
        public String version = "Unknown";
    }

    public ArrayList<AboutInfo> aboutInfo = new ArrayList<>();

    public About(Plugin plugin)
    {
        plugin.addConnectListener(this);

        command = "/about";
        help = "/about <version> - setting information about WPKG Rat (visible in /rat-list command)";
    }

    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        if (checkArgs(args, 1) == 0)
        {
            if (ClientUtils.isRat(client, (RatPlugin) plugin.getInstance()))
            {
                AboutInfo about = aboutInfo.get(client.id);
                about.version = args[0];
                client.send("Done");
            }
            else client.send("Error: client is not RAT");
        }
    }
    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        aboutInfo.add(new AboutInfo());
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        aboutInfo.remove(client.id);
    }

}
