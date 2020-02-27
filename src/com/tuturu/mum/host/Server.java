package com.tuturu.mum.host;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            while (true) {
                Socket client = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                executor.execute(new ClientThread(client, in, out, clients));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
