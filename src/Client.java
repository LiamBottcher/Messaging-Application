package src;

import java.io.*;
import java.net.*;

public class Client {

    private Connection connection;

    public void connect(String host, int port, String name, MessageListener listener) throws IOException {
        Socket socket = new Socket(host, port);
        connection = new Connection(socket, listener, name);
        new Thread(connection).start();
    }

    public void sendMessage(String msg) {
        if (connection != null) {
            connection.write(msg); // Connection will prepend the name
        }
    }

    public void disconnect() {
        if (connection != null) {
            connection.closeEverything();
        }
    }
}

interface MessageListener {
    void onMessageReceived(String message);
}

class Connection implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private MessageListener listener;
    private String name; // store the user's name

    public Connection(Socket socket, MessageListener listener, String name) {
        this.socket = socket;
        this.listener = listener;
        this.name = name;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Optional: send a "joined" message
            write("[" + name + " has joined the chat]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Prepend name automatically
    public void write(String message) {
        try {
            writer.write(name + ": " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if (listener != null) {
                    listener.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onMessageReceived("SERVER_DISCONNECTED");
            }
        } finally {
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {}
    }
}
