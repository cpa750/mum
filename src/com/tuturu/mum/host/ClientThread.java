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

    public ClientThread(Socket socket,
                        ConcurrentHashMap<String, Socket> clients)
                        throws IOException
    {
        this.socket = socket;
        this.in = new ObjectInputStream(
                socket.getInputStream()
        );
        this.out = new ObjectOutputStream(
                socket.getOutputStream()
        );
        this.clients = clients;
    }

    @Override
    public void run()
    {
        try {
            Message message = (Message) this.in.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void reply(Message rec) throws ClassNotFoundException, IOException
    {
        Message message = (Message) this.in.readObject();
        MessageType mType = rec.getType();
        switch (mType) {
            case CONNECTION_REQUEST:
                this.out.writeObject(this.newConnection(rec));
                break;
            case MULTICHAT:
                this.sendMultiChat(rec);
                break;
        }
    }

    private Message newConnection(Message rec)
    {
        String username = rec.getContent();
        if (this.isValidUsername(username)) {
            this.clients.put(username, this.socket);
            return new Message(MessageStatus.OK, MessageType.CONNECTION_ACCEPT, "");
        } else {
            return new Message(MessageStatus.ERROR, MessageType.CONNECTION_REFUSE,
                               "Username not unique");
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
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private boolean isValidUsername(String in)
    {
        return this.clients.containsKey(in);
    }
}
