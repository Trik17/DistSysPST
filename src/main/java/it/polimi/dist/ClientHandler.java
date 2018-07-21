package it.polimi.dist;

import it.polimi.dist.Messages.Message;

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

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());//forced to be before than ObjectInputStream otherwise there is a deadlock (no bug)
        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.provaOut = new PrintWriter(this.socket.getOutputStream());
        this.provaIn = new Scanner(this.socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Message message = (Message) in.readObject();
                    System.out.println("Message received");
                    server.getMulticastHandler().sendMulti(message);
                    System.out.println("sent multi message");
                    //server.addMsgQueue(message);

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
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        }
    }

