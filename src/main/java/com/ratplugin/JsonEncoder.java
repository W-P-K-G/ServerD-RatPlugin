package com.ratplugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverd.client.Client;
import com.serverd.plugin.Encoder;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.UpdateIDListener;

import java.util.ArrayList;

public class JsonEncoder extends Encoder implements UpdateIDListener, ConnectListener
{
    static ObjectMapper objectMapper = new ObjectMapper();

    ArrayList<Integer> exclude = new ArrayList<Integer>();

    public void addExclude(int id)
    {
        exclude.add(id);
    }

    public void removeExclude(int id)
    {
        exclude.remove(exclude.lastIndexOf(id));
    }

    @Override
    public String encode(String message, Client client)
    {
        if (message.startsWith("{") || exclude.lastIndexOf(client.id) != -1)
            return message;
        else
        {
            OutputMap map = new OutputMap();
            map.output = message;

            try {
                return objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String decode(String message, Client client)
    {
        if (exclude.lastIndexOf(client.id) != -1)
            return message;

        if (message.startsWith("{") )
        {
            try
            {
                CommandMap commandMap = new CommandMap();
                commandMap = objectMapper.readValue(message,CommandMap.class);

                if (!commandMap.authtoken.equals(RatPlugin.AUTHTOKEN))
                {
                    client.log.log("Command have invalid authtoken... disonnecting...");
                    return "/disconnect";
                }

                String command = (commandMap.targetid == -1 ? "" : "/to " + commandMap.targetid + " ")
                        + commandMap.command + " " + String.join(" ",commandMap.args);

                return command;
            }
            catch (JsonProcessingException e)
            {
                e.printStackTrace();
                return "";
            }
        }
        else return message;
    }

    @Override
    public void updateID(Plugin plugin, int oldid, int newid)
    {
        //excludes
        if (exclude.size() > 0)
        {
            int index = exclude.lastIndexOf(oldid);
            if (index == -1)
                return;

            exclude.set(index, newid);
        }
    }

    @Override
    public void onConnect(Plugin plugin, Client client) {}

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        int i;
        if ((i = exclude.lastIndexOf(client.id)) != -1)
            exclude.remove(i);
    }
}
