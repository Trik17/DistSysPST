package it.polimi.dist.Model;

public class AckJoinMessage extends Message{

    private int numberOfServers;

    public AckJoinMessage(int serverNumber, int numberOfServers) {
        super(serverNumber);
        this.numberOfServers = numberOfServers;
        this.isNetMessage = true;
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServerNumber() == -1)
            logic.setServerNumber(numberOfServers);

    }
}
