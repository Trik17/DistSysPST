package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.VectoUtil;

import java.util.ArrayList;

public class Acknowledge extends Message {
    protected long writeTimestamp;
    protected int writeServerNumber;

    public Acknowledge(int serverNumber){
        super(serverNumber);
    }

    public void fillReferences(long writeTimestamp,int writeServerNumber){
        this.writeServerNumber = writeServerNumber;
        this.writeTimestamp = writeTimestamp;
    }

    public long getWriteTimestamp() {
        return writeTimestamp;
    }

    public void setWriteTimestamp(long writeTimestamp) {
        this.writeTimestamp = writeTimestamp;
    }

    public int getWriteServerNumber() {
        return writeServerNumber;
    }

    public void execute(Logic logic) {
        synchronized (this) {
            for (int i = 0; i < logic.getWriteBuffer().size(); i++) {
                if (logic.getWriteBuffer().get(i).timestamp == this.writeTimestamp
                        && logic.getWriteBuffer().get(i).serverNumber == this.writeServerNumber) {
                    ArrayList<Integer> ackNotReceived2 = logic.getWriteBuffer().get(i).ackNotReceived;
                    ackNotReceived2.set(serverNumber, ackNotReceived2.get(serverNumber) - 1);
                }
            }
            /*if(logic.getAckBuffer().contains(this))
            return;*/
            for (int j = 0; j < logic.getAckBuffer().size(); j++) {
                if (timestamp == logic.getAckBuffer().get(j).getTimeStamp()
                        && serverNumber == logic.getAckBuffer().get(j).getServerNumber())
                    return;
            }
            for (int j = 0; j < logic.getPerformedWrites().size(); j++) {
                if (writeTimestamp == logic.getPerformedWrites().get(j).getTimeStamp()
                        && writeServerNumber == logic.getPerformedWrites().get(j).getServerNumber())
                    return;
            }
            if (serverNumber != logic.getServerNumber())
                VectoUtil.addOne(logic, this.serverNumber);
            logic.getAckBuffer().add(this);
            logic.checkAckBuffer();
        }
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nACK WRITE \nWrite Timestamp: " + String.valueOf(writeTimestamp) + "\nWrite ServerPackage Number: " + String.valueOf(writeServerNumber) + super.toString();
    }
}
