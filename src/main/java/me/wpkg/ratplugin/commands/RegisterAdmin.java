package me.wpkg.ratplugin.commands;

import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.ExecutionController;

import me.wpkg.ratplugin.AdminSessionManager;
import me.wpkg.ratplugin.RatPlugin;
import me.wpkg.ratplugin.utils.ClientUtils;
import me.wpkg.ratplugin.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class RegisterAdmin extends Command implements ExecutionController, ConnectListener
{
    private final ArrayList<Integer> authorized = new ArrayList<>();

    public RegisterAdmin(Plugin plugin)
    {
        command = "/registeradmin";
        help = "/registeradmin <password> - registers admin in UDP";
        plugin.addExecutionController(this);
        plugin.addConnectListener(this);
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();

        if (checkArgs(args,client,1) == 0)
        {
            if (!Utils.sha256(String.join(" ",args)).equals(instance.passwordHash))
            {
                client.send("[WRONG_PASSWORD]");
                ClientManager.delete(client.getID());
            }
            else
            {
                ClientUtils.removeFromRatList(client,instance);

                if (!ClientUtils.isAdmin(client,instance))
                {
                    ClientUtils.makeAdmin(client);
                    ClientUtils.addToAdminList(client,instance);

                    AdminSessionManager.startSessionExpireTimer(plugin, client);
                }

                authorized.add(client.getID());

                client.send("[REGISTER_SUCCESS]");
            }
        }
        else client.send("[BAD_REQUEST]");
    }

    @Override
    public boolean controlCommand(String command, String[] args, Client client, Plugin plugin) throws IOException
    {
        if (authorized.lastIndexOf(client.getID()) == -1
                && !command.matches("/registeradmin|/disconnect")
                && client.getProtocol() == Client.Protocol.UDP)
        {
            client.send("[NOT_AUTHORIZED]");
            return false;
        }
        else return true;
    }

    @Override
    public void onConnect(Plugin plugin, Client client) {}

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        if (authorized.lastIndexOf(client.getID()) != -1)
            authorized.remove(authorized.lastIndexOf(client.getID()));
    }

}
