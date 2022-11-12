package me.wpkg.ratplugin.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static me.wpkg.ratplugin.utils.Utils.objectMapper;

public class ConnectTime extends Command implements ConnectListener
{

    public static class ConnectInfo
    {
        public String date,time;

        public ConnectInfo()
        {
            this.date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            this.time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        }
    }

    HashMap<Integer,ConnectInfo> infos = new HashMap<>();

    public ConnectTime(Plugin plugin)
    {
        command = "/connect-time";
        help = "/connect-time <id> - shows client connect date";

        plugin.addConnectListener(this);
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        if (checkArgs(args,client,1) == 0)
        {
            try
            {
                client.send(objectMapper.writeValueAsString(infos.get(Integer.parseInt(args[0]))));
            }
            catch (JsonProcessingException e)
            {
                client.send("Error" + e.getMessage());
            }
        }
    }


    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        infos.put(client.getID(),new ConnectInfo());
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        infos.remove(client.getID());
    }
}
