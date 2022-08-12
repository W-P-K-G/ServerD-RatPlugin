package com.ratplugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.serverd.client.Client;
import com.serverd.client.ClientManager;

import static com.ratplugin.utils.Tools.*;

public class ClientUtils
{

    public static class ClientObject
    {
        public String name;
        public int id;

        public boolean joined;

        public String version;

        public ClientObject(int id,String name,boolean joined,String version)
        {
            this.id = id;
            this.name = name;
            this.joined = joined;
            this.version = version;
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

            clientmap.clients[i] = new ClientObject(c.id,c.name,c.joinedid != -1,"Unknown");
        }

        try
        {
            return objectMapper.writeValueAsString(clientmap);
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

    public static void makeRat(Client client)
    {
        client.name = "RAT " + client.id;
        client.log.setName("RAT Thread " + client.id);
        client.programlog.setName("RAT Program " + client.id);
    }

    public static void addToRatList(Client client,RatPlugin instance)
    {
        instance.ratsID.add(client.id);
    }

    public static void removeFromRatList(Client client,RatPlugin instance)
    {
        if (isRat(client,instance))
            instance.ratsID.remove(instance.ratsID.lastIndexOf(client.id));
    }

    public static boolean isAdmin(Client client,RatPlugin instance)
    {
        return instance.adminsID.lastIndexOf(client.id) != -1;
    }

    public static void makeAdmin(Client client,RatPlugin instance,boolean json)
    {
//        if (json)
//            client.setEncoder(instance.jsonEncoder);

        client.name = "Admin " + client.id;
        client.log.setName("Admin Thread " + client.id);
        client.programlog.setName("Admin Program " + client.id);
    }

    public static void addToAdminList(Client client,RatPlugin instance)
    {
        instance.adminsID.add(client.id);
    }

    public static void removeFromAdminList(Client client,RatPlugin instance)
    {
        if (isAdmin(client,instance))
            instance.adminsID.remove(instance.adminsID.lastIndexOf(client.id));
    }
}
