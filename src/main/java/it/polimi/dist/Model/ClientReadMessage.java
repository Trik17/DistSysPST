package it.polimi.dist.Model;

import it.polimi.dist.Client;

import java.io.IOException;
import java.util.Scanner;

public class ClientReadMessage extends ClientMessage {
    private int result;


    public ClientReadMessage() {
        super();
    }

    @Override
    public void inputFromClient(Client client) throws IOException {
        System.out.println("Insert the data ID you want to read");
        Scanner scanner = new Scanner(System.in);
        key = scanner.next();
        client.sendToServer(this);
        client.receiveRead();
    }

    @Override
    public void execute(Logic logic) {
        result = logic.getServer().getStorage().read(key);
        logic.getServer().getClientHandler().sendToClient(this);
    }
}
