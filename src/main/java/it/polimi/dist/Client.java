package it.polimi.dist;

import java.io.IOException;
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
    private Scanner socketIn;
    private PrintWriter socketOut;
    private Scanner stIn = new Scanner(System.in);

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socketclient = new Socket(ip, port);
        this.socketIn = new Scanner(socketclient.getInputStream());
        this.socketOut = new PrintWriter(socketclient.getOutputStream());
    }

    private void startClient() throws IOException {
        try {
            while (true) {
                String inputLine = stIn.nextLine();
                socketOut.println(inputLine);
                socketOut.flush();
                String socketLine = socketIn.nextLine();
                System.out.println(socketLine);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection closed");
        } finally {
            stIn.close();
            socketIn.close();
            socketOut.close();
            socketclient.close();
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(InetAddress.getLocalHost().getHostAddress(), 1330);
            System.out.println("Connection established");
            client.startClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
