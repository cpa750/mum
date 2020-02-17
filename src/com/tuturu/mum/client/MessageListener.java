package com.tuturu.mum.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MessageListener implements java.lang.Runnable
{
    private final DataInputStream in;
    private final Queue<String> messages = new LinkedList<>();

    public MessageListener(DataInputStream din)
    {
        this.in = din;
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
        this.messages.add(message);
        if (this.messages.size() > 50) this.messages.remove();

        // Clears the console
        System.out.print("\033[H\033[2J");
        for (String s: this.messages) System.out.println(s);
    }
}
