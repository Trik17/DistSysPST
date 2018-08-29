package it.polimi.dist.ServerPackage;

import it.polimi.dist.Messages.Message;
import it.polimi.dist.Messages.RemoveMessage;
import it.polimi.dist.Messages.WriteMessage;

import java.util.ArrayList;

public class TimerThread extends Thread {

    private Server server;
    private Message messageToResend;
    private int toSleep = 5000;
    private int retransmissionThreshold = 2;


    public TimerThread(Message message, Server server) {
        this.messageToResend = message;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.sleep(toSleep);
            if (messageToResend instanceof WriteMessage && !server.getLogic().isStopped()) {
                WriteMessage message = findMessage(); //find the messageToResend in the Write Buffer
                if (checkFailedServers(message)){
                    //remove server
                    System.out.println("!!!!!REMOVING SERVER!!!!!");
                    int failedServerNumber = message.getAckNotReceived().indexOf(retransmissionThreshold);
                    RemoveMessage removeMessage = new RemoveMessage(server.getLogic().getServerNumber(),failedServerNumber);
                    server.getLogic().removeServer(removeMessage);
                    for (int i = 0; i < message.getAckNotReceived().size(); i++) {
                        message.getAckNotReceived().set(i, 0);
                    }
                    return;
                }
                else {
                    updateMissingAcks(message); //add 1 to all position of ackNotReceived
                }
                for (int i = 0; i < server.getLogic().getWriteBuffer().size(); i++) {
                    if (server.getLogic().getWriteBuffer().get(i).getTimeStamp() == messageToResend.getTimeStamp()
                            && server.getLogic().getWriteBuffer().get(i).getServerNumber() == messageToResend.getServerNumber()){
                        System.out.println("------MESSAGE RETRANSMITTED------ \n" + messageToResend.toString());
                        server.sendMulti(server.getLogic().getWriteBuffer().get(i));
                        //break;
                        return;
                    }
                }
            }
            System.out.println("NOT FOUND");
            server.sendMulti(messageToResend); //for join message
            //server.sendMulti(message);
            //if I have not already received all acks (and so the timerThread is still alive)
            // resend the messageToResend (only join/write)
        } catch (InterruptedException e) {
            System.out.println("No retransmission, timer interrupted");
        }
    }


    private boolean checkFailedServers(WriteMessage message) {
        System.out.println("Missing Acks: [" + message.arrayToString(message.getAckNotReceived()) + "]");
        return message.getAckNotReceived().contains(retransmissionThreshold);
    }
    private void updateMissingAcks(WriteMessage message) {
        ArrayList<Integer> ackNotReceived = message.getAckNotReceived();
        for (int i = 0; i < server.getLogic().getVectorClock().size(); i++) {
            ackNotReceived.set(i, ackNotReceived.get(i) + 1);
        }
    }

    private WriteMessage findMessage() {
        WriteMessage message;
        for (int i = 0; i < server.getLogic().getWriteBuffer().size(); i++) {
            message = server.getLogic().getWriteBuffer().get(i);
            if (message.getTimeStamp() == messageToResend.getTimeStamp()
                    && message.getServerNumber() == messageToResend.getServerNumber()) {
                return message;
            }
        }
        return null;
    }

    public Message getMessageToResend() {
        return messageToResend;
    }
}

//todo retransmission of all the old messages with timer alive
