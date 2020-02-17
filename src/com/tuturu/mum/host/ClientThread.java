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
    private String username;
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

    private void recievedMessage()
    {
        for (Map.Entry<String, ClientThread> entry: Server.clients.entrySet())
        {
            ClientThread c = entry.getValue();
            if (c != this)
                c.sendToClient(this.rec);
        }
    }

    private boolean isCommand(String s)
    {
        return s.charAt(0) == Server.commandPrefix;
    }

    @Override
    public void run()
    {
        this.sendToClient("CONN_ACCEPT" + "," + "Server" + "," + "");
        while (this.connected &&
                this.isAlive())
        {
            try
            {
                rec = in.readUTF();
                if (this.isCommand(this.rec)) this.parseCommand(this.rec);
                else this.recievedMessage();
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
