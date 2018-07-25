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
        if(logic.writeBuffer.contains(this))
            return;
        */
        //this for avoid the readding of a write already present in the buffer
        for (int i = 0; i < logic.getWriteBuffer().size(); i++) {
            if (logic.getWriteBuffer().get(i).timestamp == this.timestamp
                    && logic.getWriteBuffer().get(i).serverNumber == this.serverNumber){
                sendAck(logic);
                return;
            }
        }
        logic.getWriteBuffer().add(this);
        sendAck(logic);
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        server.getLogic().getRetransmissionTimers().put(this, timerThread);// add to HashMap in Logic with Message-Timer
        timerThread.start();

    }



    private void sendAck(Logic logic){
        Acknowledgement ack = new Acknowledgement(logic.getServerNumber());
        ack.fillReferences(this.timestamp,this.serverNumber);
        ack.setVectorClock(VectoClockUtil.addOne(logic));
        logic.getServer().sendMulti(ack);
        if (logic.getServerNumber() != serverNumber){// the server which sent this message has already started the timer
            retransmission(logic.getServer());
        }

    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nWRITE MESSAGE" + super.toString();
    }
}
