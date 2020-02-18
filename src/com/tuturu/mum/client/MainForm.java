package com.tuturu.mum.client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainForm extends JFrame
{
    private JTextField hostnameField;
    private JTextField portField;
    private JTextField usernameField;
    private JButton connectButton;
    private JTextArea messageArea;
    private JButton sendButton;
    private JTextField messageTextField;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JScrollPane messageScrollPane;
    private JPanel messagePanel;

    public static void main(String[] args) {
        new MainForm().createUIComponents();
    }

    private void createUIComponents()
    {
        final int width = 800;
        final int height = 400;

        formPanel.setBounds(0, 10, width, height);

        mainPanel.setBounds(0, 0, width, height);
        messageArea.setBounds(0, 20, width, (height - 10));
        messageArea.setPreferredSize(new Dimension(width, (height - 10)));
        messageArea.setEditable(false);

        DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        this.setTitle("M(ostly) U(seless) M(essenger)");

        /*
         * Connect button click event
         */
        connectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                messageArea.append(messageTextField.getText() + '\n');
                messageArea.setRows(messageArea.getRows() + 1);
            }
        });

        /*
         * Message button click event
         */
        sendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                messageArea.append(messageTextField.getText() + '\n');
                messageArea.setRows(messageArea.getRows() + 1);
            }
        });

        this.messageTextField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent keyEvent){}
            @Override
            public void keyPressed(KeyEvent keyEvent){}

            @Override
            public void keyReleased(KeyEvent keyEvent)
            {
                if(keyEvent.getKeyCode() == Event.ENTER && !messageTextField.getText().isEmpty()) {
                    messageArea.append(messageTextField.getText() + '\n');
                    messageArea.setRows(messageArea.getRows() + 1);
                    messageTextField.setText("");
                }
            }
        });

        this.setContentPane(this.mainPanel);
        this.pack();
        this.setVisible(true);
    }
}
