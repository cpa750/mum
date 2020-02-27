package com.tuturu.mum.client;

import com.tuturu.mum.util.Message;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageListener implements java.lang.Runnable
{
    private final ObjectInputStream in;
    private final JTextArea messageArea;

    public MessageListener(ObjectInputStream din, JTextArea messageArea)
    {
        this.in = din;
        this.messageArea = messageArea;
    }
    public void run()
    {
        while (!Thread.interrupted())
        {
            try
            {
                Message message = (Message) this.in.readObject();
                this.receivedMessage(message);
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void receivedMessage(Message message)
    {
        String content = message.getContent();
        String username = message.getUsername();
        System.out.println(username + ": " + content);
        this.messageArea.append(username + ": " + content + '\n');
    }
}
