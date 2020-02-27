package com.tuturu.mum.host;

import com.tuturu.mum.util.Message;
import com.tuturu.mum.util.MessageStatus;
import com.tuturu.mum.util.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ClientThread implements java.lang.Runnable
{
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ConcurrentHashMap<String, Socket> clients;
    private final Socket socket;
    private String username;

    public ClientThread(Socket socket, ObjectInputStream in,
                        ObjectOutputStream out, ConcurrentHashMap<String,
                        Socket> clients)
    {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.clients = clients;
    }

    @Override
    public void run()
    {
        try {
            Message message = (Message) this.in.readObject();
            this.reply(message);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void reply(Message rec) throws IOException
    {
        MessageType mType = rec.getType();
        switch (mType) {
            case CONNECTION_REQUEST:
                Message toSend = this.newConnection(rec);
                this.out.writeObject(toSend);
                this.out.flush();
                break;
            case MULTICHAT:
                this.sendMultiChat(rec);
                break;
            case CONNECTION_END:
                this.disconnect();
                break;
        }
    }

    private Message newConnection(Message rec)
    {
        String username = rec.getUsername();
        if (this.isValidUsername(username)) {
            this.clients.put(username, this.socket);
            this.username = username;
            return new Message(MessageStatus.OK, MessageType.CONNECTION_ACCEPT,
                               "", "Server");
        } else {
            return new Message(MessageStatus.ERROR, MessageType.CONNECTION_REFUSE,
                               "Username not unique", "Server");
        }
    }

    private void sendMultiChat(Message rec)
    {
        try {
            for (Map.Entry<String, Socket> entry: this.clients.entrySet()) {
                Socket s = entry.getValue();
                ObjectOutputStream out = new ObjectOutputStream(
                        s.getOutputStream());
                out.writeObject(rec);
                out.flush();
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void disconnect()
    {
        try {
            this.socket.close();
            this.clients.remove(this.username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUsername(String in)
    {
        return !this.clients.containsKey(in);
    }
}
