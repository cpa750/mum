package mum;

import java.io.*;
import java.net.*;

public class Client
{
    public static void main(String[] args)
    {
        int port = Integer.parseInt(args[1]);
        int len = 1024;

        try (
            DatagramSocket client = new DatagramSocket();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));
            )
        {
            InetAddress hostname = InetAddress.getByName(args[0]);
            String userIn;
            Message toSend = new Message();
            Message fromServer = new Message();
            String recStr;
            String foreignUsername;

            System.out.print("Username: ");
            String username = in.readLine();

            do
            {
                System.out.format("%s:\t", username);
                userIn = in.readLine();

                toSend.message = userIn;
                toSend.username = username;

                byte[] mByteArray = new byte[1024];
                mByteArray = Serializer.serialize(toSend);

                DatagramPacket out = new DatagramPacket(mByteArray, mByteArray.length,
                                                         hostname, port);
                client.send(out);

                byte buf[] = new byte[len];
                DatagramPacket rec = new DatagramPacket(buf, len);
                client.receive(rec);
                
                fromServer = (Message) Serializer.deserialize(buf);
                recStr = fromServer.message;
                foreignUsername = fromServer.username;

                System.out.format("%s: %s\n", foreignUsername, recStr);
                
            } while (!recStr.equals("dc"));
        }
        catch (UnknownHostException e)
        {
            System.err.format("Error: %s", e.getMessage());
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

