package it.polimi.dist.Model;

public class JoinMessage extends Message{


    public JoinMessage() {
        super(-1);
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServer().getServerNumber() != 0){
        int serverNumber = logic.getServer().getServerNumber();
        int numberOfServers = logic.getVectorClock().size();
        AckJoinMessage joinMessage = new AckJoinMessage(serverNumber, numberOfServers);


    }
}
