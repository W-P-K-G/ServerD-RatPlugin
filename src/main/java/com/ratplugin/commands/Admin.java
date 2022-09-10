package com.ratplugin.commands;

import com.ratplugin.ClientUtils;
import com.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;

import java.io.IOException;

public class Admin extends Command
{
    public Admin()
    {
        command = "/admin";
        help = "/admin <with-json (optional)> - Makes admin from client";
    }

    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();

        client.send("Switched to admin mode");

        ClientUtils.removeFromRatList(client,instance);

        client.name = "Admin " + client.id + " (From terminal)";
        client.log.setName("Admin Thread " + client.id);
        client.programlog.setName("Admin Program " + client.id);

//        if (args.length >= 1)
//            if (args[0].equals("with-json"))
//                client.setEncoder(instance.jsonEncoder);
    }
}
