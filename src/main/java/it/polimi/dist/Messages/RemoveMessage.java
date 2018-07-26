package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;

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
}
