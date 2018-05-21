package it.polimi.dist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    private Server server;

    private Socket socket;
    private PrintWriter provaOut;
    private Scanner provaIn;
    private ObjectOutputStream out;//canali client
    private ObjectInputStream in;

    public ClientHandler(Socket socket, Socket groupSocket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());//forced to be before than ObjectInputStream otherwise there is a deadlock (no bug)
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.provaOut = new PrintWriter(this.socket.getOutputStream());
        this.provaIn = new Scanner(this.socket.getInputStream());
    }

    public void run() {
        try {
            while (true) {
                try {
                    Message msg = (Message) in.readObject();
                    server.addMsgQueue(msg);

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
                socket.close();
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        }
    }

