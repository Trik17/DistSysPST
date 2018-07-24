package it.polimi.dist.Model;

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

    public void execute(Logic logic) {
        //otherwise:
        /*if(logic.ackBuffer.contains(this)) //does it works?todo
            return;
        */
        //todo
        logic.getAckBuffer().add(this);
        logic.checkAckBuffer();
    }
}
