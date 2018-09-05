package it.polimi.dist.ServerPackage;


import java.io.*;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import it.polimi.dist.Messages.Message;

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
        System.out.println("Multicast Handler online");
        while (true) {
            try {
                //Create buffer to receive datagram
                byte[] buffer = new byte[bufferSize];
                DatagramPacket datagram = new DatagramPacket(buffer, bufferSize);
                multiSocket.receive(datagram);
                //System.out.println("Packet received");

                //Deserialize object
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message message = (Message) ois.readObject();
                System.out.println(message.toString());
                server.getLogic().receive(message);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
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
            DatagramPacket packet = new DatagramPacket(data, data.length, server.getGroup(), multiSocket.getLocalPort());

            //Send data
            multiSocket.send(packet);
            //System.out.println("Sent multi message");
            message.retransmission(server); //retransmission of the write/join message

         } catch (IOException e) {
        e.printStackTrace();
    }
     }


}

