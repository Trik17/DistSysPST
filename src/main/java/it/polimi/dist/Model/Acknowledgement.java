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
        for (int i = 0; i < logic.ackBuffer.size(); i++) {
            if (logic.ackBuffer.get(i).timestamp==this.timestamp
                    && logic.ackBuffer.get(i).serverNumber==this.serverNumber)
                return;
        }
        //todo
        logic.ackBuffer.add(this);
    }
}
