package it.polimi.dist;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable  {
    private Socket socket;
    private PrintWriter out;
    private Scanner in;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
    }

    public void run() {
        try {
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                }
                else {
                    out.println("Ok");
                    out.flush();
                }
            }
            in.close();
            out.close();
            socket.close();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        }
    }

