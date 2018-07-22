package it.polimi.dist.Model;

import it.polimi.dist.Client;

import java.io.IOException;

public abstract class ClientMessage extends Message {

    public ClientMessage() {
        super(-1);
    }

    public abstract void inputFromClient(Client client) throws IOException;
}
