package it.polimi.dist.Model;

import it.polimi.dist.Server;

public class TimerThread extends Thread {

    private Server server;
    private Message message;
    private int toSleep = 10000;


    public TimerThread(Message message, Server server) {
        this.message = message;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.sleep(toSleep);
            server.sendMulti(message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
