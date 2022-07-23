package com.ratplugin.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.UpdateIDListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Info extends Command implements ConnectListener
{
    static ObjectMapper objectMapper = new ObjectMapper();

    public class ConnectInfo
    {
        public String date,time;

        public ConnectInfo()
        {
            this.date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            this.time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        }
    }

    ArrayList<ConnectInfo> infos = new ArrayList<>();

    public Info(Plugin plugin)
    {
        command = "/info";

        help = "/info <id> - shows client connect date";

        plugin.addConnectListener(this);
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin)
    {
        if (checkArgs(args,1) == 0)
        {
            try {
                client.send(objectMapper.writeValueAsString(infos.get(Integer.parseInt(args[0]))));
            } catch (JsonProcessingException e) {
                client.send("Error" + e.getMessage());
            }
        }
    }


    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        infos.add(new ConnectInfo());
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        infos.remove(client.id);
    }
}
