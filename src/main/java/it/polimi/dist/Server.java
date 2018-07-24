package it.polimi.dist;

import it.polimi.dist.Messages.JoinMessage;
import it.polimi.dist.Model.Logic;
import it.polimi.dist.Messages.Message;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;


public class Server  implements Runnable{

    private DataStorage storage;
    private ExecutorService executor;
    private int port; //port for Client-Server Socket
    private int multiPort; //port for Server Multicast Socket
    private InetAddress group;
    private Logic logic;
    private MulticastHandler multicastHandler;
    private ClientHandler clientHandler;



    public Server(int port, int multiPort, String groupIP) throws UnknownHostException {
        this.storage = new DataStorage();
        this.port = port;
        this.multiPort = multiPort;
        this.executor = Executors.newCachedThreadPool();
        this.group = InetAddress.getByName(groupIP);
        this.logic = new Logic(this, -1);
    }

    public Server(int port, int multiPort, String groupIP, int serverNumber) throws UnknownHostException {
        this.storage = new DataStorage();
        this.port = port;
        this.multiPort = multiPort;
        this.executor = Executors.newCachedThreadPool();
        this.group = InetAddress.getByName(groupIP);
        this.logic = new Logic(this, serverNumber);
    }

    public void startServer() {
        System.out.println("Server online");

        MulticastSocket multiSocket;
        try {
            //multicast connection
            multiSocket = new MulticastSocket(multiPort);
            multiSocket.setInterface(this.getIP());
            multiSocket.joinGroup(group);
            System.out.println("Server joined");

            multicastHandler = new MulticastHandler(this, multiSocket);
            new Thread(multicastHandler).start(); //start Multicast Handling
            if (logic.getServerNumber() == -1) {
                JoinMessage joinMessage = new JoinMessage();
                sendMulti(joinMessage);
            }
            new Thread(this).start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("error");
        }
    }

    @Override
    public void run() {
        while (true) {
            //Client-Server connections
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(port);
                Socket socket = serverSocket.accept();
                clientHandler = new ClientHandler(this, socket);
                executor.submit(clientHandler);
                if (serverSocket.isClosed()) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

    }

    public InetAddress getIP() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                //if(i.getHostAddress().contains("192.168.43")) //todo for tethering
                    if(i.getHostAddress().contains("192.168.1")) //todo at home
                    return i;
            }
        }
        return null;
    }

    synchronized public void sendMulti(Message message){
        multicastHandler.sendMulti(message);
    }


    public DataStorage getStorage() {
        return storage;
    }

    public InetAddress getgroup() {
        return group;
    }


    public MulticastHandler getMulticastHandler() {
        return multicastHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }



    public void setStorage(Map<String, Integer> storage) {
        this.storage.setData(storage);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMultiPort() {
        return multiPort;
    }

    public void setMultiPort(int multiPort) {
        this.multiPort = multiPort;
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public void setMulticastHandler(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }




    public static void main(String[] args) {
        Server server = null;
        try {
            System.out.println("Are you the first server? \n(1) Yes - (2) No");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            if (choice == 1)
                server = new Server(9334, 9000,"225.4.5.6", 0);
            else
                server = new Server(9334, 9000,"225.4.5.6");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server.startServer();
    }



}
