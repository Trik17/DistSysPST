package it.polimi.dist;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server  {

    private DataStorage data;
    private ExecutorService executor;
    private int port; //port for Server-Client Socket
    private int multiPort; //port for Server Multicast Socket


    public Server(int port) {

    }

    public Server(int port, int multiPort) {
        //this.data = new DataStorage();
        this.port = port;

    }

    public int read(String dataId) {
        return this.data.read(dataId);
    }

    public void write(String dataId, int newData) {
        this.data.write(dataId,newData);
    }

    public void startServer() {
        executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        MulticastSocket multiSocket;
        try {
            serverSocket = new ServerSocket(port);
            multiSocket = new MulticastSocket(multiPort);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server online");
        try {
            Socket groupSocket = new Socket(multiSocket.getInetAddress(), multiPort);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,groupSocket);
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

    public static void main(String[] args) {
        Server server = new Server(9334, 9000);
        server.startServer();
    }
}
