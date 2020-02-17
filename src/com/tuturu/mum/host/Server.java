package com.tuturu.mum.host;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{
    public static ConcurrentHashMap<String, ClientThread> clients = new ConcurrentHashMap<>();
    public static char commandPrefix = '/';

    public static void main(String[] args)
    {
        int port = Integer.parseInt(args[0]);
        try ( ServerSocket serverSocket = new ServerSocket(port) )
        {
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                // Message format: type,username,content
                String[] split = in.readUTF().split(",", 2);
                String messageType = split[0];
                String username = split[1];

                if (messageType.equals("CONN_REQ") && !clients.containsKey(username))
                {
                    ClientThread clientThread =
                            new ClientThread(clientSocket, in, out, username);
                    new Thread(clientThread).start();
                }
                else
                {
                    out.writeUTF("CONN_REFUSED" + "," + "Server" + "");
                    out.flush();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

};
