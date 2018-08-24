package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.VectoUtil;

public class Acknowledgement extends Message {
    protected long writeTimestamp;
    protected int writeServerNumber;

    public Acknowledgement(int serverNumber){
        super(serverNumber);
    }

    public void fillReferences(long writeTimestamp,int writeServerNumber){
        this.writeServerNumber=writeServerNumber;
        this.writeTimestamp=writeTimestamp;
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
        /*if(logic.getAckBuffer().contains(this))
            return;*/
        for (int j = 0; j < logic.getAckBuffer().size(); j++) {
            if (timestamp==logic.getAckBuffer().get(j).getTimeStamp()
                    && serverNumber == logic.getAckBuffer().get(j).getServerNumber()){
                System.out.println("RETURN ACK perché già in ack buffer");
                return;
            }
        }
        for (int j = 0; j < logic.getPerformedWrites().size(); j++) {
            if (writeTimestamp==logic.getPerformedWrites().get(j).getTimeStamp()
                    && writeServerNumber == logic.getPerformedWrites().get(j).getServerNumber()){
                System.out.println("RETURN ACK perché già performata la write");
                return;
            }
        }
        if (serverNumber!=logic.getServerNumber())
            VectoUtil.addOne(logic,this.serverNumber);
        logic.getAckBuffer().add(this);
        System.out.println("ADDED ACK to ack buffer " + getWriteTimestamp() + "  " + arrayToString(getVectorClock()));
        logic.checkAckBuffer();
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nACK WRITE \nWrite Timestamp: " + String.valueOf(writeTimestamp) + "\nWrite ServerPackage Number: " + String.valueOf(writeServerNumber) + super.toString();
    }
}