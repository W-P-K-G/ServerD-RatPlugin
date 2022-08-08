package com.ratplugin;

import com.serverd.client.Client;

import com.serverd.plugin.Encoder;

//\r\n replace for telnet
public class NewlineReplacer extends Encoder
{
    public String encode(String message, Client client)
    {
        return message;
    }

    public String decode(String message, Client client)
    {
        return message.replaceAll("\n","").replaceAll("\r","");
    }
}
