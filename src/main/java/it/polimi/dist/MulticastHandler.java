package it.polimi.dist;


import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import it.polimi.dist.Model.Message;
import java.io.IOException;
import java.io.ObjectInputStream;


public class MulticastHandler implements Runnable {

    private Server server;
    private MulticastSocket multiSocket;
    private final int bufferSize = 1024 * 4; //bisogna capire quanto grandi possono essere i pacchetti messaggio


    public MulticastHandler(Server server, MulticastSocket multiSocket) throws IOException {
        this.server = server;
        this.multiSocket = multiSocket;

    }

    @Override
    public void  run(){

        /*Thread outputThread = new Thread();
        outputThread.run();*/ //useless?

        while (true) {
            try {
                System.out.println("Multicast Handler");
                //Create buffer to receive datagram
                byte[] buffer = new byte[bufferSize];
                DatagramPacket datagram = new DatagramPacket(buffer, bufferSize);
                multiSocket.receive(datagram);
                System.out.println("Packet receive");

                //Deserialize object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message message = (Message) ois.readObject();
                server.getLogic().receive(message);
                //System.out.println(message.toString());
                System.out.println("Multicast Message Received");

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
        }

    }


     public void sendMulti(Message message){
         try {
            //Serialize data message
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, server.getgroup(), multiSocket.getLocalPort());

            //Send data
            multiSocket.send(packet);
            System.out.println("Sent multi message");


         } catch (IOException e) {
        e.printStackTrace();
    }
     }

    public void ackManagement(){
        //Message ack = new Acknowledgement(server);
        //sendMulti(ack);

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

