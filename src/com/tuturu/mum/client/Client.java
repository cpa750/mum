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
        System.out.println("Socket started");
        InputStream i = server.getInputStream();
        OutputStream o = server.getOutputStream();
        System.out.println("Got IOStreams");
        try {
            this.in = new ObjectInputStream(i);
            this.out = new ObjectOutputStream(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setUsername(username);
        System.out.println("Username set");
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
            this.out.flush();
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
            this.out.flush();
            System.out.println("Message written to socket");
        }
    }

    private void setUsername(String username) throws IOException
    {
        Message m = new Message(MessageStatus.OK,
                                MessageType.CONNECTION_REQUEST,
                                "", username);
        this.out.writeObject(m);
        this.out.flush();

        try {
            Message rec = (Message) this.in.readObject();
            if (rec.getType() == MessageType.CONNECTION_ACCEPT) {
                this.username = username;
                this.messageArea.append("Connected!");
            } else
                this.messageArea.append(rec.getUsername() + ": " + rec.getContent());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
                this.out.flush();
        }
    }
}
