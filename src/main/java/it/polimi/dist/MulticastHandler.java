package it.polimi.dist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MulticastHandler {

    private Server server;

    private Socket groupSocket;
    private ObjectInputStream multiIn; //canali server
    private ObjectOutputStream multiOut;


    public MulticastHandler(Server server, Socket groupSocket) throws IOException {
        this.server = server;
        this.groupSocket = groupSocket;
        this.multiOut = new ObjectOutputStream(this.groupSocket.getOutputStream());
        this.multiIn = new ObjectInputStream(this.groupSocket.getInputStream());
    }

    public void start() {
        try {
            Thread outputThread = new Thread();
            outputThread.run();
            while (true) {
                try {
                    Message msg = (Message) multiIn.readObject();
                    server.addMsgQueue(msg);
                    int timeStamp = msg.getTimeStamp();
                    calculateClock(msg.getTimeStamp());
                    ackManagement();



                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
           /* while (true) {
                String line = provaIn.nextLine();
                out.w
                System.out.println(line);
                if (line.equals("quit")) {
                    break;
                }
                else {
                    provaOut.println("Ok");
                    provaOut.flush();
                }
            }
            provaIn.close();
            provaOut.close();*/
                groupSocket.close();
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
     public void  run(){
        while (true){

        }
     }

    public void ackManagement(){
        Message ack = new Acknowledgement(server);
        try {
            multiOut.writeObject(ack);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void calculateClock(int tstamp){
        int lclock = server.getLamportClock();

        if (lclock > tstamp)
            server.setLamportClock(lclock +1);
        else
            server.setLamportClock(tstamp +1);
     }

}

