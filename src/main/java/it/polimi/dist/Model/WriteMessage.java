package it.polimi.dist.Model;

public class WriteMessage extends Message {

    public WriteMessage(int serverNumber) {
        super(serverNumber);
    }

    public void execute(Logic logic) {
        //otherwise:
        /*
        if(logic.writeBuffer.contains(this)) //does it works?todo
            return;
        */
        for (int i = 0; i < logic.writeBuffer.size(); i++) {
            if (logic.writeBuffer.get(i).timestamp == this.timestamp
                    && logic.writeBuffer.get(i).serverNumber == this.serverNumber){
                sendAck(logic);
                return;
            }
        }
        logic.writeBuffer.add(this);
        sendAck(logic);
    }

    private void sendAck(Logic logic){
        Acknowledgement ack= new Acknowledgement(logic.serverNumber);
        ack.fillReferences(this.timestamp,this.serverNumber);
        ack.setVectorClock(VectoClockUtil.addOne(logic));
        logic.ackBuffer.add(ack);
        logic.getServer().sendMulti(ack);
    }

}
