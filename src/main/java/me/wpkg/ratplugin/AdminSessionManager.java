package me.wpkg.ratplugin;

import com.serverd.client.Client;
import com.serverd.client.ClientManager;
import com.serverd.log.Log;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.listener.ConnectListener;
import com.serverd.plugin.listener.ExecutionController;
import com.serverd.util.Util;

import me.wpkg.ratplugin.utils.ClientUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class AdminSessionManager implements ConnectListener
{
    public static final int SESSION_TIME = 5 * 60;

    private static final Log log = new Log("Admin Session Manager");

    @Override
    public void onConnect(Plugin plugin, Client client)
    {
        RatPlugin instance = (RatPlugin) plugin.getInstance();

        if (ClientUtils.isAdmin(client,instance))
            startSessionExpireTimer(plugin, client);
    }

    public static void startSessionExpireTimer(Plugin plugin,Client client)
    {
        new Thread(() -> {
            AtomicInteger time = new AtomicInteger(SESSION_TIME);

            ExecutionController controller = (command,args,cl,plug) -> {
                time.set(SESSION_TIME);
                return true;
            };
            plugin.addExecutionController(controller);

            while (client.isConnected())
            {
                time.getAndDecrement();
                Util.sleep(1000);

                if (time.get() <= 0)
                {
                    log.info("Session expired for admin " + client.getID());
                    ClientManager.delete(client.getID());
                    break;
                }
            }

            plugin.removeExecutionController(controller);
        },"Session Manager").start();
    }

    @Override
    public void onDisconnect(Plugin plugin, Client client) {}
}
