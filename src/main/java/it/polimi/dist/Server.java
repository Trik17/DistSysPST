package it.polimi.dist;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server  {

    private DataStorage data;
    private ExecutorService executor;
    private int port;
    private ServerSocket serverSocket;


    public Server(int port) {
        this.data = new DataStorage();
        this.port = port;
    }

    public int read(String dataId) {
        return 0;
    }

    public void write(String dataId, int newData) {

    }

    public void startServer() {
        executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server online");
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                executor.submit(clientHandler);
                if (serverSocket.isClosed()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("error");
        }
        executor.shutdown();
    }

    public static void main() {
        Server server = new Server(1330);
        server.startServer();
    }
}
