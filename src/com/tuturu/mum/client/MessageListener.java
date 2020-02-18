package com.tuturu.mum.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MessageListener implements java.lang.Runnable
{
    private final DataInputStream in;
    private final Queue<String> messages = new LinkedList<>();
    private final JTextArea messageArea;

    public MessageListener(DataInputStream din, JTextArea messageArea)
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
                String message = this.in.readUTF();
                this.receivedMessage(message);
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
    private void receivedMessage(String message)
    {
        System.out.println(message);
        String[] splitMessage = message.split(",", 3);
        String username = splitMessage[1];
        String content = splitMessage[2];
        this.messageArea.append(username + ": " + content + '\n');
    }
}
