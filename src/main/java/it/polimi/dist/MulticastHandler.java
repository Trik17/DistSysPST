package it.polimi.dist;

<<<<<<< HEAD
import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
=======
import it.polimi.dist.Messages.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
>>>>>>> e498f01db29b6bc8529250328f340c8528f5a21a

public class MulticastHandler implements Runnable {

    private Server server;
    private MulticastSocket multiSocket;
    private final int bufferSize = 1024 * 4; //bisogna capire quanto grandi possono essere i pacchetti messaggio


    public MulticastHandler(Server server, MulticastSocket multiSocket) throws IOException {
        this.server = server;
        this.multiSocket = multiSocket;

    }

    public void start() {
<<<<<<< HEAD
        /*Thread outputThread = new Thread();
        outputThread.run();*/ //useless?

        while (true) {
            try {

                //Create buffer to receive datagram
                byte[] buffer = new byte[bufferSize];
                DatagramPacket datagram = new DatagramPacket(buffer, bufferSize);
                multiSocket.receive(datagram);
                System.out.println("packet received");

                //Deserialize object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message msg = (Message) ois.readObject();
=======
        try {
            Thread outputThread = new Thread();
            outputThread.run();
            while (true) {
                try {
                    Message msg = (Message) multiIn.readObject();
                    //server.addMsgQueue(msg);
                    long timeStamp = msg.getTimeStamp();
                    //calculateClock(msg.getTimeStamp());
                    ackManagement();
>>>>>>> e498f01db29b6bc8529250328f340c8528f5a21a


                server.addMsgQueue(msg);
                int timeStamp = msg.getTimeStamp();
                calculateClock(msg.getTimeStamp());
                ackManagement();



            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
            multiSocket.close();
        }
    }
     public void  run(){
        while (true){

        }
     }

     public void sendMulti(Message message){
         try {

         //Serialize data message
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(message);
         byte[] data = baos.toByteArray();
         DatagramPacket packet = new DatagramPacket(data, data.length, server.getgroup(), multiSocket.getPort());

         //Send data
         multiSocket.send(packet);

         } catch (IOException e) {
        e.printStackTrace();
    }
     }

    public void ackManagement(){
        Message ack = new Acknowledgement(server);
        sendMulti(ack);

    }
/*
    public void calculateClock(int tstamp){
        int lclock = server.getLamportClock();

        if (lclock > tstamp)
            server.setLamportClock(lclock +1);
        else
            server.setLamportClock(tstamp +1);
     }
*/
}

