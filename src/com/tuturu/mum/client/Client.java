package com.tuturu.mum.client;

import java.io.*;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        Client c = new Client();
        c.run(hostname, port);
    }

    private void run(String hostname, int port)
    {
        try (
                Socket server = new Socket(hostname, port);
                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                BufferedReader userIn = new BufferedReader(
                        new InputStreamReader(System.in))
        )
        {
            String username = this.setUsername(userIn, out, in);
            System.out.format("Client connected to %s:%d\n", hostname, port);
            Thread messageListener = new Thread(
                    new MessageListener(in)
            );
            messageListener.start();

            String msg;
            do
            {
                System.out.print("You: ");
                msg = userIn.readLine();
                sendMessage(out, "USR_MSG", username, msg);
            } while (!msg.equals("/disconnect"));
            messageListener.interrupt();
        }
        catch (IOException e)
        {
            System.err.format("Error: %s", e.getMessage());
        }

    }

    private void sendMessage(DataOutputStream out, String mType,
                             String username, String content) throws IOException
    {
        String message = mType + "," + username + "," + content;
        out.writeUTF(message);
        out.flush();
    }

    private String setUsername(BufferedReader userIn, DataOutputStream out,
                               DataInputStream in) throws IOException
    {
        String username;
        String[] rec;
        do
        {
            System.out.print("Enter username: ");
            username = userIn.readLine();
            sendMessage(out, "CONN_REQ", username, "");
            rec = in.readUTF().split(",", 2);
        } while (!rec[0].equals("CONN_ACCEPT"));
        return username;
    }
}
