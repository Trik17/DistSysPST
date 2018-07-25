package it.polimi.dist.ServerPackage;

import it.polimi.dist.Messages.Message;

public class TimerThread extends Thread {

    private Server server;
    private Message message;
    private int toSleep = 5000;


    public TimerThread(Message message, Server server) {
        this.message = message;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.sleep(toSleep);
            server.sendMulti(message);
            //if I have not already received all acks (and so the timerthread is still alive)
            // resend the message (only join/write)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
