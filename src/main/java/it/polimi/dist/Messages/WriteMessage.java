package it.polimi.dist.Messages;

import it.polimi.dist.Logic;

public class WriteMessage extends Message {

    public WriteMessage(int serverNumber) {
        super(serverNumber);
    }

    public void execute(Logic logic) {
        //TODO
        logic.writeBuffer.add(this);
        Acknowledgement ack= new Acknowledgement(logic.serverNumber);
        ack.fillReferences(this.timestamp,this.serverNumber);
        logic.ackBuffer.add(ack);
        logic.getServer().sendMulti(ack);
    }

}
