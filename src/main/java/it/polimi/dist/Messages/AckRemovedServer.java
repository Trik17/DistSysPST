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
        synchronized (logic) {//todo controlla bene questa
            for (int i = 0; i < logic.getMyRemoveMessages().size(); i++) {
                if (logic.getMyRemoveMessages().get(i).getServerNumber() == this.removeMessage.getServerNumber() &&
                        this.removeMessage.getTimeStamp() == logic.getMyRemoveMessages().get(i).getTimeStamp()){
                    for (int j = 0; i < logic.getAckRemovedServers().size(); j++) {
                        if (logic.getAckRemovedServers().get(j).getServerNumber() == this.removeMessage.getServerNumber())
                            return;
                    }
                    logic.getAckRemovedServers().add(this);
                    logic.checkAckRemove(removeMessage);
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
