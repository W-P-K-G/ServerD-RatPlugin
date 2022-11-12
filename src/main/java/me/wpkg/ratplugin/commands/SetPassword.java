package me.wpkg.ratplugin.commands;

import com.serverd.client.Client;
import com.serverd.plugin.Plugin;
import com.serverd.plugin.command.Command;
import me.wpkg.ratplugin.RatPlugin;
import me.wpkg.ratplugin.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;

public class SetPassword extends Command
{
    public SetPassword()
    {
        command = "/setpassword";
        help = "/setpassword <new password> - settings new password";
    }
    @Override
    public void execute(String[] args, Client client, Plugin plugin) throws IOException
    {
        if (checkArgs(args,client,1) == 0)
        {
            RatPlugin instance = (RatPlugin) plugin.getInstance();

            String hash = Utils.sha256(String.join(" ",args));

            Files.write(instance.passwordFile.toPath(),Utils.hexToBytes(hash));
            instance.passwordHash = hash;

            client.send("Done");
        }
    }
}
