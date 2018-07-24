package it.polimi.dist.Messages;

import it.polimi.dist.Model.Logic;
import it.polimi.dist.Model.TimerThread;
import it.polimi.dist.Model.VectoClockUtil;
import it.polimi.dist.Server;

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
        for (int i = 0; i < logic.getWriteBuffer().size(); i++) {
            if (logic.getWriteBuffer().get(i).timestamp == this.timestamp
                    && logic.getWriteBuffer().get(i).serverNumber == this.serverNumber){
                //sendAck(logic);
                return;
            }
        }
        logic.getWriteBuffer().add(this);
        sendAck(logic);
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        timerThread.start();
        // add to Hash Map in Logic with Message - Timer

    }



    private void sendAck(Logic logic){
        Acknowledgement ack= new Acknowledgement(logic.getServerNumber());
        ack.fillReferences(this.timestamp,this.serverNumber);
        ack.setVectorClock(VectoClockUtil.addOne(logic));
        logic.getAckBuffer().add(ack);
        logic.getServer().sendMulti(ack);
    }

    @Override
    public String toString() {
        return "WRITE MESSAGE" + super.toString();
    }
}
