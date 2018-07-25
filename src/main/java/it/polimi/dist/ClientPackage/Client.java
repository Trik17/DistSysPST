package it.polimi.dist.ClientPackage;

import it.polimi.dist.Messages.ClientMessage;
import it.polimi.dist.Messages.ClientReadMessage;
import it.polimi.dist.Messages.ClientWriteMessage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Client {
    private String ip;
    private int port;
    private Socket socketClient;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private Scanner scanner;

    public Client(String ip, int port, Scanner scanner) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socketClient = new Socket(ip, port);
        this.objOut = new ObjectOutputStream(socketClient.getOutputStream());
        this.objIn = new ObjectInputStream(socketClient.getInputStream());
        this.scanner = scanner;
    }

    private void startClient() throws IOException {
        try {
            while (true) {
                ClientMessage clientMessage = createMessage();
                clientMessage.inputFromClient(this);
                System.out.println("Sent Client message");

            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection closed");
        } finally {
            socketClient.close();
        }
    }

    public void createRandomMessages() {
        try {
            new Thread(new ClientReadThread(this)).start();

            do {
                System.out.println("Type anything and press Enter to start...");
                String startInput = scanner.next();

                Random rnd = new Random();

                //Communicating with the server
                for (int i = 0; i < 20; i++) {
                    Thread.sleep(50); //message frequency
                    String id = String.valueOf(rnd.nextInt(5));
                    int value = rnd.nextInt(50);
                    ClientMessage clientMessage;
                    if (Math.random() < 0.5) {
                        clientMessage = new ClientReadMessage(id);
                        System.out.println("AUTO-READ ID: " + id);
                    } else {
                        clientMessage = new ClientWriteMessage(id, value);
                        System.out.println("AUTO-WRITE ID: " + id + ", VALUE: " + value);
                    }
                    sendToServer(clientMessage);
                }
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public ClientMessage createMessage(){
        System.out.println("Which action do you want to execute? \n(r) Read - (w) Write");
        String choice = scanner.next();
        if ("w".equals(choice)) {
            ClientWriteMessage clientWriteMessage = new ClientWriteMessage();
            return clientWriteMessage;

        } else if ("r".equals(choice)) {
            ClientReadMessage clientReadMessage = new ClientReadMessage();
            return clientReadMessage;

        } else {
            System.out.println("Invalid Input, ");
            return createMessage();
        }
    }

    public void receiveRead() {
        try {
            ClientReadMessage receivedMessage = (ClientReadMessage) objIn.readObject();
            if (receivedMessage.getResult().equals("null"))
                System.out.println("Data id Not found");
            else
                System.out.println("Value: " + receivedMessage.getResult());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(ClientMessage clientMessage){
        try {
            objOut.writeObject(clientMessage);
            objOut.flush();
            objOut.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocketClient() {
        return socketClient;
    }

    public void setSocketClient(Socket socketClient) {
        this.socketClient = socketClient;
    }


    public ObjectInputStream getObjIn() {
        return objIn;
    }

    public void setObjIn(ObjectInputStream objIn) {
        this.objIn = objIn;
    }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }

    public void setObjOut(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }


    public static void main(String[] args) {
        try {
            System.out.println("Insert Server ip Address");
            Scanner scanner = new Scanner(System.in);
            String ip = scanner.next();
            //String ip = InetAddress.getLocalHost().getHostAddress();
            Client client = new Client(ip, 9334, scanner);
            System.out.println("Connection established");
            System.out.println("Select Client Mode: \n(1) Manual (2) Auto");
            int choice = scanner.nextInt();
            if (choice == 2) {
                client.createRandomMessages();
            } else {
                client.startClient();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("The ip address does not exist");
        }
    }

}
