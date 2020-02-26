package com.tuturu.mum.host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server
{
    public static void main(String[] args)
    {
        ConcurrentHashMap<String, Socket> clients = new ConcurrentHashMap<>();
        int port = Integer.parseInt(args[0]);
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try ( ServerSocket serverSocket = new ServerSocket(port) )
        {
            do {
                Socket client = serverSocket.accept();
                executor.execute( new ClientThread(client, clients) );
            } while (clients.size() > 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
