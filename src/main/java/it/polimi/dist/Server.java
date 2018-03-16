package it.polimi.dist;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server  {

    private int numberOfServer; //TODO

    private DataStorage data;
    private ExecutorService executor;
    private int port; //port for Server-Client Socket
    private int multiPort; //port for Server Multicast Socket
    private List<Message> queue;

    private int processNumber;

    private int lamportClock;


    public Server(int port) {

    }



    public Server(int port, int multiPort) {
        //this.data = new DataStorage();
        this.port = port;
        this.lamportClock = 1;


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
            MulticastHandler multicastHandler = new MulticastHandler(this, groupSocket);
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

    public void checkQueue(){
        //TODO
    }

    public static void main(String[] args) {
        Server server = new Server(9334, 9000);
        server.startServer();
    }

    public void addElementQueue(Message msg) {
        int tstamp = msg.getTimeStamp();
        int index = queue.size() -1;

        if (queue.isEmpty()) {
            queue.add(msg);
            return;
        }
        while (index >= 0){

            if (tstamp > queue.get(index).getTimeStamp()) {
                queue.add(index +1, msg);
                return;
            }

            index--;
        }

        queue.add(0, msg);
    }

    public void setLamportClock(int lamportClock) {
        this.lamportClock = lamportClock;
    }

    public int getLamportClock() {
        return lamportClock;
    }

    public int getProcessNumber() {
        return processNumber;
    }
}
