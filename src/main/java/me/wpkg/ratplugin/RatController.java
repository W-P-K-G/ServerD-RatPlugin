package me.wpkg.ratplugin;

import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.listener.ExecutionController;

import me.wpkg.ratplugin.utils.ClientUtils;

import java.io.IOException;

public class RatController implements ExecutionController
{
    static String[] blockedCommands = {"/to","/join","/unjoin","/close","/plugin"};

    @Override
    public boolean controlCommand(String command, String[] args, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();
        if (ClientUtils.isRat(client,instance))
        {
            for (String comm : blockedCommands)
            {
                if (command.equals(comm))
                {
                    client.send("Access denied");
                    return false;
                }
            }
        }
        return true;
    }
}
