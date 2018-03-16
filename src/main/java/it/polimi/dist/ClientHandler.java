package it.polimi.dist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    private ObjectInputStream multiIn;
    private ObjectOutputStream multiOut;
    private Socket socket;
    private Socket groupSocket;
    private PrintWriter provaOut;
    private Scanner provaIn;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket, Socket groupSocket) throws IOException {
        this.socket = socket;
        this.groupSocket = groupSocket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());//forced to be before than ObjectInputStream otherwise there is a deadlock (no bug)
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.multiOut = new ObjectOutputStream(this.groupSocket.getOutputStream());
        this.multiIn = new ObjectInputStream(this.groupSocket.getInputStream());
        this.provaOut = new PrintWriter(this.socket.getOutputStream());
        this.provaIn = new Scanner(this.socket.getInputStream());
    }

    public void run() {
        try {
            while (true) {
                String line = provaIn.nextLine();
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
            provaOut.close();
            socket.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        }
    }

