package it.polimi.dist.ClientPackage;

import it.polimi.dist.ClientPackage.Client;

public class ClientReadThread implements Runnable {
    private Client client;

    public ClientReadThread(Client client) {
        this.client = client;
    }


    @Override
    public void run() {
        while (true){
            client.receiveRead();

        }
    }
}
