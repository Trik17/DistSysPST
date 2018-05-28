package it.polimi.dist;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class Server  {

    private int numberOfActiveProcesses;

    private int numberOfServer; //TODO

    private DataStorage data;
    private ExecutorService executor;
    private int port; //port for Server-Client Socket
    private int multiPort; //port for Server Multicast Socket
    private List<Message> msgQueue;
    private Map<Integer, List<Acknowledgement>> ackQueue;

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

    public void execute(Message msg){

        if(allAcksReceived(msg)) {

            deliver(msg);
            clearAcks(msg);
        }

    }


    public boolean allAcksReceived(Message msg) {
        Integer ID = msg.getID();
        int numberOfAcks = ackQueue.get(ID).size();
        List<Acknowledgement> acks = ackQueue.get(ID);

        for (int i = 0; i < numberOfAcks -2; i++) {
            for (int j = i +1; j < numberOfAcks -1; j++){
                if (acks.get(i).getProcessNumber() == acks.get(j).getProcessNumber()) {
                    acks.remove(j);
                    numberOfAcks--;
                }
            }
        }

        return numberOfAcks == numberOfActiveProcesses;

    }

    public static void main(String[] args) {
        Server server = new Server(9334, 9000);
        server.startServer();
    }

    public void addAckQueue(Acknowledgement ack){
        Integer ID = ack.getID();

        try{

            ackQueue.get(ID).add(ack);

        }catch(NullPointerException e){

            List<Acknowledgement> acks = new ArrayList<Acknowledgement>();
            acks.add(ack);
            ackQueue.put(ID,acks);

        }
    }

    public void addMsgQueue(Message msg) {
        int tstamp = msg.getTimeStamp();
        int index = msgQueue.size() -1;

        while (index >= 0){

            if (tstamp > msgQueue.get(index).getTimeStamp()) {
                msgQueue.add(index +1, msg);
                return;
            }

            index--;
        }

        msgQueue.add(0, msg);
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

    //in realtà il contenuto del messaggio andrebbe messo nella coda dell'applicazione che lo deve eseguire, ma siccome
    //noi non abbiamo applicazioni ma solo un metodo execute nel msg, l'ho lasciato così
    public void deliver(Message msg){
        msg.execute(this);
    }

    public void clearAcks(Message msg){
        Integer ID = msg.getID();
        ackQueue.remove(ID);
    }

}
