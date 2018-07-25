package it.polimi.dist.ClientPackage;

import it.polimi.dist.ClientPackage.Client;

public class ClientReadThread extends Thread {
    private Client client;

    public ClientReadThread(Client client) {
        this.client = client;
    }

    @Override
    public synchronized void start() {
        while (true){
            client.receiveRead();
        }

    }
}
