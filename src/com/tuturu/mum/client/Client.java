package com.tuturu.mum.client;

import java.io.*;
import java.net.Socket;

public class Client
{
    private String username;
    private final Thread messageListener;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Client(String hostname, int port, String username) throws IOException
    {
        Socket server = new Socket(hostname, port);
        this.in = new DataInputStream(server.getInputStream());
        this.out = new DataOutputStream(server.getOutputStream());
        this.setUsername(username);
        this.messageListener = new Thread(
                new MessageListener(in)
        );
        messageListener.start();
    }

    public void disconnect()
    {
        this.messageListener.interrupt();
        try
        {
            this.in.close();
            this.out.close();
        }
        catch (IOException e)
        {
            System.err.format("Error: %s", e.getMessage());
        }
    }

    private void sendMessage(DataOutputStream out, String mType,
                             String content) throws IOException
    {
        String message = mType + "," + this.username + "," + content;
        out.writeUTF(message);
        out.flush();
    }

    private void setUsername(String username) throws IOException
    {
        this.sendMessage(this.out, "CONN_REQ", "");
        String[] res = this.in.readUTF().split(",", 2);
        String res_type = res[0];
        String res_message = res[2];

        if (res_type.equals("CONN_REFUSED"))
            throw new IOException("Error: connection refused - "+res_message);
        else if (res_type.equals("CONN_ACCEPT"))
            this.username = username;
        /*
         * Successful connection to the server is dependent on
         * whether the username is unique or not
         */
    }
}
