package it.polimi.dist.ServerPackage;

import it.polimi.dist.Messages.Message;

public class TimerThread extends Thread {

    private Server server;
    private Message message;
    private int toSleep = 5000;
    private int retransmissionThreshold = 5;


    public TimerThread(Message message, Server server) {
        this.message = message;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.sleep(toSleep);
            int numberOfRetransmission = message.getNumberOfRetransmission()
            if (numberOfRetransmission < retransmissionThreshold){
                server.sendMulti(message);
                System.out.println("------MESSAGE RETRANSMITTED------ \n" + message.toString());
                //if I have not already received all acks (and so the timerthread is still alive)
                // resend the message (only join/write)
                message.setNumberOfRetransmission(numberOfRetransmission + 1);
            }
            else {
                server.getLogic().removeServer();
            }

        } catch (InterruptedException e) {
            System.out.println("No retransmission, timer interrupted");
        }
    }
}
