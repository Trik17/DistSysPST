package it.polimi.dist;

import it.polimi.dist.Model.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client implements Serializable {
    private String ip;
    private int port;
    private Socket socketclient;
    private PrintWriter provaOut;
    private Scanner provaIn;
    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;
    private Scanner stIn = new Scanner(System.in);

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socketclient = new Socket(ip, port);
        this.objOut = new ObjectOutputStream(socketclient.getOutputStream());
        this.objIn = new ObjectInputStream(socketclient.getInputStream());
        this.provaOut = new PrintWriter(socketclient.getOutputStream());
        this.provaIn = new Scanner(socketclient.getInputStream());
    }

    private void startClient() throws IOException {
        try {
            System.out.println("Eccomi");
            while (true) {
            ClientMessage clientMessage = createMessage();
            clientMessage.inputFromClient(this);
            System.out.println("Sent Client message");


                /*String inputLine = stIn.nextLine();
                provaOut.println(inputLine);
                provaOut.flush();*/
                //String socketLine = provaIn.nextLine();
                //System.out.println(socketLine);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection closed");
        } finally {
            stIn.close();
            provaIn.close();
            provaOut.close();
            socketclient.close();
        }
    }

    public ClientMessage createMessage(){
        System.out.println("Which action do you want to execute? \n (R) Read - (W) Write");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.next();
        if ("W".equals(choice)) {
            ClientWriteMessage clientWriteMessage = new ClientWriteMessage();
            return clientWriteMessage;

        } else if ("R".equals(choice)) {
            ClientReadMessage clientReadMessage = new ClientReadMessage();
            return clientReadMessage;

        } else {
            System.out.println("Invalid Input, ");
            return createMessage();
        }
    }

    public void receiveRead() {
        try {
            Message receivedMessage = (Message) objIn.readObject();
            if (receivedMessage.getData() == Integer.parseInt(null))
                System.out.println("The selected data id does not already exist");
            else
                System.out.println("Value: " + receivedMessage.getData());
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

    public Socket getSocketclient() {
        return socketclient;
    }

    public void setSocketclient(Socket socketclient) {
        this.socketclient = socketclient;
    }

    public PrintWriter getProvaOut() {
        return provaOut;
    }

    public void setProvaOut(PrintWriter provaOut) {
        this.provaOut = provaOut;
    }

    public Scanner getProvaIn() {
        return provaIn;
    }

    public void setProvaIn(Scanner provaIn) {
        this.provaIn = provaIn;
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

    public Scanner getStIn() {
        return stIn;
    }

    public void setStIn(Scanner stIn) {
        this.stIn = stIn;
    }


    public static void main(String[] args) {
        try {
            Client client = new Client(InetAddress.getLocalHost().getHostAddress(), 9334);
            System.out.println("Connection established");
            client.startClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
