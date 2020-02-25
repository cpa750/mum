package com.tuturu.mum.util;

import java.io.Serializable;

public class Message implements Serializable
{
    private final MessageStatus status;
    private final MessageType type;
    private final String content;

    public Message(MessageStatus status, MessageType type, String content)
    {
        this.type = type;
        this.status = status;
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
}
