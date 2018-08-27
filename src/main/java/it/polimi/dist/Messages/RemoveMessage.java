package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.VectoUtil;
import it.polimi.dist.ServerPackage.Server;
import it.polimi.dist.ServerPackage.TimerThread;

public class RemoveMessage extends Message {
    private int removedServerNumber;

    public RemoveMessage(int serverNumber, int removedServerNumber) {
        super(serverNumber);
        isRemovingMessage = true;
        this.removedServerNumber = removedServerNumber;
    }

    @Override
    public void execute(Logic logic) {
        logic.removeServer(this);
    }

    public void sendAckRemove(Logic logic){
        AckRemovedServer ack;
        if (logic.isStopped())
            ack = new AckRemovedServer(logic.getServerNumber(), this);
        else {
            if (this.removedServerNumber <= logic.getServerNumber())
                ack = new AckRemovedServer(logic.getServerNumber() + 1, this);
            else
                ack = new AckRemovedServer(logic.getServerNumber(), this);
        }
        logic.getServer().sendMulti(ack);
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        server.getLogic().setRemoveRetransmissionTimer(timerThread);
        timerThread.start();
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nREMOVE MESSAGE" + "\nRemoved Server Number:" + String.valueOf(removedServerNumber) + super.toString();
    }

    public int getRemovedServerNumber() {
        return removedServerNumber;
    }
}


