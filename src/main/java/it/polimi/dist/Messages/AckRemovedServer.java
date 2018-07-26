package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;

public class AckRemovedServer extends Message{
    private RemoveMessage removeMessage;
    private int removedServer;

    public AckRemovedServer(int serverNumber, RemoveMessage removeMessage) {
        super(serverNumber);
        isRemovingMessage=true;
        this.removeMessage=removeMessage;
    }

    @Override
    public void execute(Logic logic) {
        synchronized (logic) {
            for (int i = 0; i < logic.getRemoveMessages().size(); i++) {
                if (logic.getRemoveMessages().get(i).getServerNumber() == this.removeMessage.getServerNumber() &&
                        this.removeMessage.getTimeStamp() == logic.getRemoveMessages().get(i).getTimeStamp()) {
                    logic.getAckRemovedServers().add(this);
                    logic.checkAckRemove(removedServer);
                    return;
                }
            }
        }
    }

    public int getRemovedServer() {
        return removedServer;
    }

    public RemoveMessage getRemoveMessage() {
        return removeMessage;
    }
}
