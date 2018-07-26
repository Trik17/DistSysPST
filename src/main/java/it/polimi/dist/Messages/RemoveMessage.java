package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.VectoUtil;
import it.polimi.dist.ServerPackage.Server;
import it.polimi.dist.ServerPackage.TimerThread;

public class RemoveMessage extends Message {
    private int removedServerNumber;

    public RemoveMessage(int serverNumber, int removedServerNumber) {
        super(serverNumber);
        isRemovingMessage=true;
        this.removedServerNumber=removedServerNumber;
    }

    @Override
    public void execute(Logic logic) {
        logic.removeServer(this);
    }

    public void sendAckRemove(Logic logic){
        AckRemovedServer ack = new AckRemovedServer(logic.getServerNumber(), this);
        logic.getServer().sendMulti(ack);
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        server.getJoinHandler().setTimerJoin(timerThread);
        timerThread.start();
    }

    private RemoveMessage findRemoveMessage(Server server) {
        RemoveMessage message;
        for (int i = 0; i < server.getLogic().getWriteBuffer().size(); i++) {
            message = server.getLogic().getWriteBuffer().get(i);
            if (message.getTimeStamp() == this.getTimeStamp()
                    && message.getServerNumber() == this.getServerNumber()) {
                return message;
            }
        }
        return null;
    }
}
