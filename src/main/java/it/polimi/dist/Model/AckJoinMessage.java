package it.polimi.dist.Model;

public class AckJoinMessage extends Message{

    private int numberOfServers;

    public AckJoinMessage(int serverNumber, int numberOfServers) {
        super(serverNumber);
        this.numberOfServers = numberOfServers;
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServer().getServerNumber() == -1)
            logic = new Logic(this, this.serverNumber);//TODO bisogna dare un numero al server
    }
}
