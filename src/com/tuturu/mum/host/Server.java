package com.tuturu.mum.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{
    public static void main(String[] args)
    {
        // TODO: multithreading
        ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
        int port = Integer.parseInt(args[0]);
        try ( ServerSocket serverSocket = new ServerSocket(port) )
        {
            do {
                Socket client = serverSocket.accept();
                new ClientThread(client, clients).run();
            } while (clients.size() > 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
