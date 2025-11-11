package src;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    public static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        int idCounter = 0;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1337);
            System.out.println("Server started on port 1337");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket, idCounter++);
                clients.add(client);
                new Thread(client).start();
            }

        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        } finally {
            for (ClientHandler client : clients) {
                client.writeToSelf("Server has closed, thank you for connecting");
                client.closeEverything();
            }
        }
    }
}

class ClientHandler implements Runnable {

    private Socket socket;
    private int id;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientHandler(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToSelf(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException e) {}
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                for (ClientHandler client : Server.clients) {
                    client.writeToSelf(message); // broadcast exactly what client sent
                }
            }
        } catch (IOException e) {}
        finally {
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
