package me.wpkg.ratplugin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.wpkg.ratplugin.RatPlugin;
import me.wpkg.ratplugin.commands.About;
import com.serverd.client.Client;
import com.serverd.client.ClientManager;

import static me.wpkg.ratplugin.utils.Utils.*;

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

        for (int i = 0;i < clientmap.clients.length;i++)
        {
            Client c = ClientManager.getClient(instance.ratsID.get(i));

            About.AboutInfo aboutInfo = instance.about.aboutInfo.get(c.getID());
            clientmap.clients[i] = new ClientObject(c.getID(),c.getName(),c.isJoined(),aboutInfo.version);
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
       return instance.ratsID.lastIndexOf(client.getID()) != -1;
    }

    public static void makeRat(Client client)
    {
        client.setName("RAT " + client.getID());
        client.log.setName("RAT Thread " + client.getID());
        client.programlog.setName("RAT Program " + client.getID());
    }

    public static void addToRatList(Client client,RatPlugin instance)
    {
        instance.ratsID.add(client.getID());
    }

    public static void removeFromRatList(Client client,RatPlugin instance)
    {
        if (isRat(client,instance))
            instance.ratsID.remove(instance.ratsID.lastIndexOf(client.getID()));
    }

    public static boolean isAdmin(Client client,RatPlugin instance)
    {
        return instance.adminsID.lastIndexOf(client.getID()) != -1;
    }

    public static void makeAdmin(Client client)
    {
        client.setName("Admin " + client.getID());
        client.log.setName("Admin Thread " + client.getID());
        client.programlog.setName("Admin Program " + client.getID());
    }

    public static void makeConsoleAdmin(Client client)
    {
        client.setName("Admin " + client.getID() + " (From terminal)");
        client.log.setName("Admin Thread " + client.getID());
        client.programlog.setName("Admin Program " + client.getID());
    }

    public static void addToAdminList(Client client,RatPlugin instance)
    {
        instance.adminsID.add(client.getID());
    }

    public static void removeFromAdminList(Client client,RatPlugin instance)
    {
        if (isAdmin(client,instance))
            instance.adminsID.remove(instance.adminsID.lastIndexOf(client.getID()));
    }
}
