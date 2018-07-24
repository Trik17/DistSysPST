package it.polimi.dist.Messages;

import it.polimi.dist.Client;
import it.polimi.dist.Model.ClientMessage;
import it.polimi.dist.Model.Logic;

import java.io.IOException;
import java.util.Scanner;

public class ClientReadMessage extends ClientMessage {
    private String result;


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

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nCLIENT READ MESSAGE" + super.toString();
    }
}
