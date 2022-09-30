package me.wpkg.ratplugin.commands;

import com.serverd.plugin.listener.UpdateIDListener;
import me.wpkg.ratplugin.utils.ClientUtils;
import me.wpkg.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class About extends Command implements ConnectListener, UpdateIDListener
{

    public static class AboutInfo
    {
        public String version = "Unknown";
    }

    public HashMap<Integer, AboutInfo> aboutInfo = new HashMap<>();

    public About(Plugin plugin)
    {
        command = "/about";
        help = "/about <version> - setting information about WPKG Rat (visible in /rat-list command)";

        plugin.addConnectListener(this);
        plugin.addUpdateIDListener(this);
    }

    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();
        if (checkArgs(args,client, 1) == 0)
        {
            if (ClientUtils.isRat(client, instance))
            {
                AboutInfo about = aboutInfo.get(client.getID());
                about.version = args[0];
                client.send("Done");
            }
            else client.send("Error: client is not RAT");
        }
    }
    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        aboutInfo.put(client.getID(),new AboutInfo());
    }

    @Override
    public void updateID(Plugin plugin, int oldid, int newid)
    {
        AboutInfo info = aboutInfo.get(oldid);
        aboutInfo.remove(oldid);

        aboutInfo.put(newid,info);
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        aboutInfo.remove(client.getID());
    }

}
