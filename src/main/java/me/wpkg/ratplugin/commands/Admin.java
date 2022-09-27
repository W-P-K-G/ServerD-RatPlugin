package me.wpkg.ratplugin.commands;

import me.wpkg.ratplugin.utils.ClientUtils;
import me.wpkg.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import me.wpkg.ratplugin.utils.Utils;

import java.io.IOException;

public class Admin extends Command
{
    public Admin()
    {
        command = "/admin";
        help = "/admin <password> - Makes admin from client";
    }

    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();

        if (checkArgs(args, client, 1) == 0)
        {
            if (Utils.sha256(String.join(" ",args)).equals(instance.passwordHash))
            {
                ClientUtils.removeFromRatList(client,instance);
                ClientUtils.makeConsoleAdmin(client);

                client.send("Switched to admin mode");
            }
            else client.send("Wrong password");
        }

    }
}
