package com.tuturu.mum.client;

import java.io.DataInputStream;
import java.io.IOException;

public class MessageListener implements java.lang.Runnable
{
    private final DataInputStream in;

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
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
}
