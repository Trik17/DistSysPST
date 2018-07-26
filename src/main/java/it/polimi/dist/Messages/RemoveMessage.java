package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
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
        if (logic.isStopped())
            return;
        else
            logic.removeServer(this.removedServerNumber);
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        server.getLogic().setRemoveRetransmissionTimer(timerThread);
        timerThread.start();
    }


}
