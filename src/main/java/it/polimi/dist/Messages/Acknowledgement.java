package it.polimi.dist.Messages;

import it.polimi.dist.Model.Logic;

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

    public void setWriteServerNumber(int writeServerNumber) {
        this.writeServerNumber = writeServerNumber;
    }

    public void execute(Logic logic) {
        //otherwise:
        /*if(logic.ackBuffer.contains(this)) //does it works?todo
            return;
        */
        //todo
        logic.getAckBuffer().add(this);
        logic.checkAckBuffer();
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nACK WRITE \nWrite Timestamp: " + String.valueOf(writeTimestamp) + "\nWrite Server Number: " + String.valueOf(writeServerNumber) + super.toString();
    }
}
