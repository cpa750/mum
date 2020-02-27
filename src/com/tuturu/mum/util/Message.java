package com.tuturu.mum.util;

import java.io.Serializable;

public class Message implements Serializable
{
    private final MessageStatus status;
    private final MessageType type;
    private final String content;
    private final String username;

    public Message(MessageStatus status, MessageType type,
                   String content, String username)
    {
        this.status = status;
        this.type = type;
        this.username = username;
        this.content = content;
    }


    public MessageStatus getStatus()
    {
        return status;
    }

    public MessageType getType()
    {
        return type;
    }

    public String getContent()
    {
        return content;
    }

    public String getUsername()
    {
        return username;
    }
}
