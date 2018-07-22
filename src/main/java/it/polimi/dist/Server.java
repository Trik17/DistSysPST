package it.polimi.dist;

import it.polimi.dist.Model.Logic;
import it.polimi.dist.Model.Message;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;

//import java.util.function.Predicate;

public class Server  {

    private int numberOfActiveProcesses;

    private int numberOfServer; //TODO

    private DataStorage data;
    private ExecutorService executor;
    private int port; //port for Server-Client Socket
    private int multiPort; //port for Server Multicast Socket
    //private List<Message> msgQueue;
    //private Map<Integer, List<Acknowledgement>> ackQueue;
    private InetAddress group;
    private Logic logic;
    private MulticastHandler multicastHandler;
    private ClientHandler clientHandler;
    private int serverNumber;

    private int processNumber;

    private int lamportClock;


    public Server(int port) {

    }



    public Server(int port, int multiPort, String groupIP) throws UnknownHostException {
        //this.data = new DataStorage();
        this.port = port;
        this.lamportClock = 1;
        this.multiPort = multiPort;
        executor = Executors.newCachedThreadPool();
        this.logic = new Logic(this, this.serverNumber);//TODO bisogna dare un numero al server
        group = InetAddress.getByName(groupIP);
    }


    public int read(String dataId) {
        return this.data.read(dataId);
    }

    public void write(String dataId, int newData) {
        logic.write(dataId,newData);
    }

    public void sendMulti(Message message){
        //TODO
    }


    public DataStorage getData() {
        return data;
    }

    public void startServer() {
        System.out.println("Server online");

        ServerSocket serverSocket;
        MulticastSocket multiSocket;
        try {
            //multicast connection todo managing IP addresses for multisocket

            serverSocket = new ServerSocket(port);
            multiSocket = new MulticastSocket(multiPort);
            //multiSocket.setInterface(this.getIP());
            multiSocket.joinGroup(group); //join message?
            System.out.println("Server joined");


            multicastHandler = new MulticastHandler(this, multiSocket);
            new Thread(multicastHandler).start(); //start Multicast Handling

            while (true) {
                //Client-Server connections
                Socket socket = serverSocket.accept();
                clientHandler = new ClientHandler(this, socket);
                executor.submit(clientHandler);
                if (serverSocket.isClosed()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("error");
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
               // if(i.getHostAddress().contains("192.168.43"))
                    //if(i.getHostAddress().contains("192.168.1"))
                    return i;
            }
        }
        return null;
    }

/*
    public boolean allAcksReceived(Message msg) {
        Integer ID = msg.getID();
        int numberOfAcks = ackQueue.get(ID).size();
        List<Acknowledgement> acks = ackQueue.get(ID);

        for (int i = 0; i < numberOfAcks -2; i++) {
            for (int j = i +1; j < numberOfAcks -1; j++){
                if (acks.get(i).getServerNumber() == acks.get(j).getServerNumber()) {
                    acks.remove(j);
                    numberOfAcks--;
                }
            }
        }

        return numberOfAcks == numberOfActiveProcesses;

    }

*/

    /*
>>>>>>> e498f01db29b6bc8529250328f340c8528f5a21a
    public void addAckQueue(Acknowledgement ack){
        Integer ID = ack.getID();

        try{

            ackQueue.get(ID).add(ack);

        }catch(NullPointerException e){

            List<Acknowledgement> acks = new ArrayList<Acknowledgement>();
            acks.add(ack);
            ackQueue.put(ID,acks);

        }
    }*/
/*
    public void addMsgQueue(Message msg) {
        long tstamp = msg.getTimeStamp();
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
*/
/*
    public void setLamportClock(int lamportClock) {
        this.lamportClock = lamportClock;
    }
*/
    public int getLamportClock() {
        return lamportClock;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    //in realtà il contenuto del messaggio andrebbe messo nella coda dell'applicazione che lo deve eseguire, ma siccome
    //noi non abbiamo applicazioni ma solo un metodo execute nel msg, l'ho lasciato così
    public void deliver(Message msg){
        msg.execute(this.logic);
    }
    /*
    public void clearAcks(Message msg){
        Integer ID = msg.getID();
        ackQueue.remove(ID);
    }*/


    public InetAddress getgroup() {
        return group;
    }


    public MulticastHandler getMulticastHandler() {
        return multicastHandler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public int getNumberOfActiveProcesses() {
        return numberOfActiveProcesses;
    }

    public void setNumberOfActiveProcesses(int numberOfActiveProcesses) {
        this.numberOfActiveProcesses = numberOfActiveProcesses;
    }

    public int getNumberOfServer() {
        return numberOfServer;
    }

    public void setNumberOfServer(int numberOfServer) {
        this.numberOfServer = numberOfServer;
    }

    public void setData(DataStorage data) {
        this.data = data;
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

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }

    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }

    public void setLamportClock(int lamportClock) {
        this.lamportClock = lamportClock;
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server(9334, 9000,"225.4.5.6");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        server.startServer();
    }


}
