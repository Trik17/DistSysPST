package it.polimi.dist.Messages;

import it.polimi.dist.Server;

public class WriteMessage extends Message {

    public WriteMessage(int serverNumber) {
        super(serverNumber);
    }

    @Override
    public void execute(Server server) {
        //server.addMsgQueue(this);

    }

}
