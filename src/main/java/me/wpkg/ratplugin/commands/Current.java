package me.wpkg.ratplugin.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;

import java.io.IOException;

import static me.wpkg.ratplugin.utils.Utils.*;

public class Current extends Command
{
    public static class CurrentMap
    {
        public boolean joined = false;

        public int id = -1;
        public String name = "";
    }

    public Current()
    {
        command = "/current";
        help = "/current - shows info about joined client";
    }

    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        Client joiner = client.getJoinedID() == -1 ? null : ClientManager.getClient(client.getJoinedID());
        
        CurrentMap map = new CurrentMap();
        if (joiner != null)
        {
            map.id = joiner.getID();
            map.name = joiner.getName();
            map.joined = true;
        }

        try
        {
            client.send(objectMapper.writeValueAsString(map));
        }
        catch (JsonProcessingException e)
        {
            client.send("Error");
            e.printStackTrace();
        }
    }
}
