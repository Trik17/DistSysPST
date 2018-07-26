package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.VectoUtil;

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
}
