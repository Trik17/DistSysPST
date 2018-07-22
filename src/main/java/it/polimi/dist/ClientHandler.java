package it.polimi.dist;

import it.polimi.dist.Model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    private Server server;

    private Socket socket;
    private ObjectOutputStream out;//canali client
    private ObjectInputStream in;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.out = new ObjectOutputStream(this.socket.getOutputStream());//forced to be before than ObjectInputStream otherwise there is a deadlock (no bug)
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    public void sendToClient(Message message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    //receive Client Messages
                    Message message = (Message) in.readObject();
                    System.out.println("Client Message received");
                    message.execute(server.getLogic());
                    System.out.println("Message processed");

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

