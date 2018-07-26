package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;

public class AckRemovedServer extends Message{

    public AckRemovedServer(int serverNumber) {
        super(serverNumber);
        isRemovingMessage=true;
    }

    @Override
    public void execute(Logic logic) {
        synchronized (logic) {
            for (int i = 0; i < logic.getAckRemovedServers().size(); i++) {
                if (logic.getAckRemovedServers().get(i).getServerNumber() == this.serverNumber)
                    return;
            }
            logic.getAckRemovedServers().add(this);
            logic.checkAckRemove();
        }
    }
}
