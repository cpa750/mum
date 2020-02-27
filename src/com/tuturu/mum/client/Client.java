package com.tuturu.mum.client;

import com.tuturu.mum.util.Message;
import com.tuturu.mum.util.MessageStatus;
import com.tuturu.mum.util.MessageType;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client
{
    private String username;
    private Thread messageListener;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final JTextArea messageArea;

    public Client(JTextArea messageArea)
    {
        this.messageArea = messageArea;
    }

    public void connect(String hostname, int port, String username) throws IOException
    {
        Socket server = new Socket(hostname, port);
        this.in = new ObjectInputStream(server.getInputStream());
        this.out = new ObjectOutputStream(server.getOutputStream());
        this.setUsername(username);
        this.messageListener = new Thread(
                new MessageListener(this.in, this.messageArea)
        );
        messageListener.start();
    }

    public void disconnect()
    {
        this.messageListener.interrupt();
        try
        {
            Message m = new Message(MessageStatus.OK,
                                    MessageType.CONNECTION_END,
                                    "", this.username);
            this.out.writeObject(m);
            this.in.close();
            this.out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException
    {
        if (message.charAt(0) == '/')
            this.command(message.substring(1));
        else {
            Message m = new Message(MessageStatus.OK,
                                    MessageType.MULTICHAT,
                                    message, this.username);
            this.out.writeObject(m);
        }
    }

    private void setUsername(String username) throws IOException
    {
        Message m = new Message(MessageStatus.OK,
                                MessageType.CONNECTION_REQUEST,
                                "", this.username);
        this.out.writeObject(m);

        try {
            m = (Message) this.in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (m.getType() == MessageType.CONNECTION_ACCEPT)
            this.username = username;
        else
            throw new IOException("Error: connection refused");
        /*
         * successful connection to the server depends on whether the
         * requested username is unique or not
         */
    }

    private void command(String in) throws IOException
    {
        switch (in) {
            case "dc":
                this.disconnect();
                break;
            case "here":
                Message m = new Message(MessageStatus.OK,
                                        MessageType.COMMAND,
                                        in, this.username);
                this.out.writeObject(m);
        }
    }
}
