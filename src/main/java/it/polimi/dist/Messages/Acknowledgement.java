package it.polimi.dist.Messages;
import it.polimi.dist.Logic;

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
        //todo
    }
}
