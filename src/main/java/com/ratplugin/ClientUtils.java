package com.ratplugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverd.client.Client;
import com.serverd.client.ClientManager;

public class ClientUtils
{
    static ObjectMapper objectMapper = new ObjectMapper();

    public static class ClientObject
    {
        public String name;
        public int id;

        public ClientObject(int id,String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    public static class ClientMap
    {
        public ClientObject[] clients;
    }

    public static String ratsListJson(RatPlugin instance)
    {
        ClientMap clientmap = new ClientMap();
        clientmap.clients = new ClientObject[instance.ratsID.size()];

        for (int i = 0;i < clientmap.clients.length;i++)
        {
            Client c = ClientManager.getClient(instance.ratsID.get(i));

            clientmap.clients[i] = new ClientObject(c.id,c.name);
        }

        try
        {
            String message = objectMapper.writeValueAsString(clientmap);

            return message;
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isRat(Client client,RatPlugin instance)
    {
       return instance.ratsID.lastIndexOf(client.id) != -1;
    }

    public static void removeFromRatList(Client client,RatPlugin instance)
    {
        if (isRat(client,instance))
            instance.ratsID.remove(client.id);
    }

    public static boolean isAdmin(Client client,RatPlugin instance)
    {
        return instance.adminsID.lastIndexOf(client.id) != -1;
    }

    public static void removeFromAdminList(Client client,RatPlugin instance)
    {
        if (isAdmin(client,instance))
            instance.adminsID.remove(client.id);
    }
}
