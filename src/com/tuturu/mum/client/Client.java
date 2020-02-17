package com.tuturu.mum.client;

import java.io.*;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        try (
                Socket server = new Socket(hostname, port);
                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                BufferedReader userIn = new BufferedReader(
                        new InputStreamReader(System.in));
        )
        {
            String[] rec;
            String msg;
            String username;

            do
            {
                System.out.print("Enter username: ");
                username = userIn.readLine();
                sendMessage(out, "CONN_REQ", username, "");
                rec = in.readUTF().split(",", 2);
            } while (!rec[0].equals("CONN_ACCEPT"));

            System.out.format("Client connected to %s:%d\n", hostname, port);

            // TODO: write MessageListenerThread
            do
            {
                System.out.print("You: ");
                msg = userIn.readLine();
                sendMessage(out, "USR_MSG", username, msg);
            } while (!msg.equals("/dc"));
        }
        catch (IOException e)
        {
            System.err.format("Error: %s", e.getMessage());
        }
    }

    private static void sendMessage(DataOutputStream out, String mType,
                             String username, String content)
    {
        try
        {
            String message = mType + "," + username + "," + content;
            out.writeUTF(message);
            out.flush();
        }
        catch (IOException e)
        {
            System.err.format("Could not send message: %s", e.getMessage());
        }
    }
}
