package mum;

import java.io.*;
import java.net.*;

public class Server
{
    public static void main(String[] args)
    {
        int port = Integer.parseInt(args[0]);
        int len = 1024;

        try (
            DatagramSocket serverSocket = new DatagramSocket(port);
            )
        {
            InetAddress clientAddr;
            int clientPort;
            String recStr;
            Message recM = new Message();
            Message toSend = new Message();
            toSend.username = "Server";
            do
            {
                byte[] buf = new byte[len];
                DatagramPacket rec = new DatagramPacket(buf, len);
                serverSocket.receive(rec);

                recM = (Message) Serializer.deserialize(buf);
                recStr = recM.message;

                clientAddr = rec.getAddress();
                clientPort = rec.getPort();

                toSend.message = recStr;
                
                byte[] mByteArray = Serializer.serialize(toSend);
                DatagramPacket out = new DatagramPacket(mByteArray,
                                                        mByteArray.length,
                                                        clientAddr, clientPort);
                serverSocket.send(out);

            } while (true);
        }
        catch (IOException e)
        {
            System.err.format("Error: %s", e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.err.format("Error: %s", e.getMessage());
        }
    }
}

