package it.polimi.dist.Model;

import it.polimi.dist.Client;

import java.io.IOException;
import java.util.Scanner;

public class ClientWriteMessage extends ClientMessage{


    public ClientWriteMessage() {
        super();
    }

    @Override
    public void inputFromClient(Client client) throws IOException {
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




}
