package it.polimi.dist.Messages;

import it.polimi.dist.Client;
import it.polimi.dist.Model.ClientMessage;
import it.polimi.dist.Model.Logic;

import java.io.IOException;
import java.util.Scanner;

public class ClientWriteMessage extends ClientMessage {


    public ClientWriteMessage() {
        super();
    }

    public ClientWriteMessage(String id, int value) {
        this.key = id;
        this.data = value;
    }

    @Override
    public void inputFromClient(Client client)  {
        System.out.println("Insert the data ID you want to modify");
        Scanner scanner = new Scanner(System.in);
        key = scanner.next();
        System.out.println("Insert the value you want to write");
        data = scanner.nextInt();
        client.sendToServer(this);
    }


    @Override
    public void execute(Logic logic) {
        logic.write(this.key, this.data);
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nCLIENT WRITE MESSAGE" + super.toString();
    }
}
