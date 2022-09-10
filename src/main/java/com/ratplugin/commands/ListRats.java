package com.ratplugin.commands;

import com.ratplugin.ClientUtils;
import com.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;

import java.io.IOException;

public class ListRats extends Command
{
    public ListRats()
    {
        command = "/rat-list";
        help = "/rat-list - Sending RAT's list in JSON";
    }
    @Override
    public void execute(String[] strings, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();

        client.send(ClientUtils.ratsListJson(instance));
    }
}
