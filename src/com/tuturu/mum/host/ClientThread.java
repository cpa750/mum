package com.tuturu.mum.host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

class ClientThread implements java.lang.Runnable
{
    private final Socket clientSocket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final String username;
    private String rec;
    private boolean connected = true;

    public ClientThread(Socket socket, DataInputStream dis, DataOutputStream dos,
                        String username)
    {
        this.clientSocket = socket;
        this.in = dis;
        this.out = dos;
        this.username = username;

        Server.clients.put(username, this);
    }

    private void receivedMessage()
    {
        System.out.println(this.rec);
        System.out.format("%s: %s\n", this.rec.split(",", 3)[1],
                          this.rec.split(",",3)[2]);
        if (this.getMessageType(this.rec).equals("USR_MSG"))
            this.sendToAllClients(this.rec);
    }

    private void sendToAllClients(String message)
    {
        for (Map.Entry<String, ClientThread> entry: Server.clients.entrySet())
        {
            ClientThread c = entry.getValue();
            if (c != this)
                c.sendToClient(message);
        }
    }

    private String getMessageType(String message)
    {
        return message.split(",")[0];
    }

    private String getMessageContent(String message)
    {
        try
        {
            return message.split(",", 3)[2];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return "";
        }
    }

    private boolean isCommand(String s)
    {
        return s.charAt(0) == Server.commandPrefix;
    }

    @Override
    public void run()
    {
        while (this.connected &&
                this.isAlive())
        {
            try
            {
                this.rec = in.readUTF();
                if (this.isCommand(this.rec)) this.parseCommand(this.rec);
                else this.receivedMessage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void parseCommand(String s)
    {
        switch (s)
        {
            case "disconnect":
                this.disconnect();
        }
    }

    private void disconnect()
    {
        Server.clients.remove(this.username);
        this.connected = false;
    }

    public void sendToClient(String message)
    {
        if (this.isAlive())
        {
            try
            {
                this.out.writeUTF(message);
                this.out.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean isAlive()
    {
        try
        {
            int TIMEOUT_MS = 300;
            return this.clientSocket.getInetAddress().isReachable(TIMEOUT_MS);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
