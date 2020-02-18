package com.tuturu.mum.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client
{
    private String username;
    private Thread messageListener;
    private DataInputStream in;
    private DataOutputStream out;
    private final JTextArea messageArea;

    public Client(JTextArea messageArea)
    {
        this.messageArea = messageArea;
    }

    public void connect(String hostname, int port, String username) throws IOException
    {
        Socket server = new Socket(hostname, port);
        this.in = new DataInputStream(server.getInputStream());
        this.out = new DataOutputStream(server.getOutputStream());
        this.setUsername(username);
        this.messageListener = new Thread(
                new MessageListener(in, this.messageArea)
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

    public void sendMessage(String mType, String content) throws IOException
    {
        String message = mType + "," + this.username + "," + content;
        this.out.writeUTF(message);
        this.out.flush();
    }

    public void sendMessage(String mType, String username,
                            String content) throws IOException
    {
        String message = mType + "," + username + "," + content;
        this.out.writeUTF(message);
        this.out.flush();
    }

    private void setUsername(String username) throws IOException
    {
        this.sendMessage("CONN_REQ", username, "ping");
        String[] res = this.in.readUTF().split(",", 3);
        for (String s : res) System.out.println(s);
        String res_type = res[0];
        String res_message = res[2];

        if (res_type.equals("CONN_REFUSED"))
            throw new IOException("Error: connection refused - "/* + res_message*/);
        else if (res_type.equals("CONN_ACCEPT"))
            this.username = username;
        /*
         * Successful connection to the server is dependent on
         * whether the username is unique or not
         */
    }
}
