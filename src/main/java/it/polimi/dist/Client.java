package it.polimi.dist;

import it.polimi.dist.Messages.WriteMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
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
            //while (true) {
                System.out.println("Eccomi");
                WriteMessage writeMessage = new WriteMessage(5);
                objOut.writeObject(writeMessage);
                objOut.flush();
                objOut.reset();
                /*String inputLine = stIn.nextLine();
                provaOut.println(inputLine);
                provaOut.flush();*/
                //String socketLine = provaIn.nextLine();
                //System.out.println(socketLine);
           // }
        } catch (NoSuchElementException e) {
            System.out.println("Connection closed");
        } finally {
            stIn.close();
            provaIn.close();
            provaOut.close();
            socketclient.close();
        }
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
