package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleGUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ===== Panels =====
        JPanel loginPanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField(20);
        JButton connectButton = new JButton("Connect to server");
        loginPanel.add(nameLabel);
        loginPanel.add(nameField);
        loginPanel.add(connectButton);

        JPanel chatPanel = new JPanel(new BorderLayout());
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        JPanel messagePanel = new JPanel(new FlowLayout());
        JTextField messageField = new JTextField(25);
        JButton sendButton = new JButton("Send");
        messagePanel.add(messageField);
        messagePanel.add(sendButton);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        frame.add(loginPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // ===== Client model =====
        Client client = new Client();

        // ===== Button Actions =====
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) return;

                try {
                    client.connect("127.0.0.1", 1337, name, new MessageListener() {
                        @Override
                        public void onMessageReceived(final String msg) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    if ("SERVER_DISCONNECTED".equals(msg)) {
                                        JOptionPane.showMessageDialog(frame, "Server has disconnected!");
                                        frame.remove(chatPanel);
                                        frame.add(loginPanel, BorderLayout.CENTER);
                                        frame.revalidate();
                                        frame.repaint();
                                    } else {
                                        chatArea.append(msg + "\n");
                                    }
                                }
                            });
                        }
                    });

                    frame.remove(loginPanel);
                    frame.add(chatPanel, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Failed to connect to server!");
                    ex.printStackTrace();
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = messageField.getText().trim();
                if (!msg.isEmpty()) {
                    client.sendMessage(msg);
                    messageField.setText("");
                }
            }
        });
    }
}
