package it.polimi.dist.Model;

public class JoinMessage extends Message {


    public JoinMessage() {
        super(-1);
        this.isNetMessage = true;
    }

    @Override
    public void execute(Logic logic) {
        if (logic.getServerNumber() != -1) {
            int serverNumber = logic.getServerNumber();
            int numberOfServers = logic.getVectorClock().size();
            AckJoinMessage joinMessage = new AckJoinMessage(serverNumber, numberOfServers, logic.getServer().getStorage());
            logic.getServer().sendMulti(joinMessage);
            logic.addServer();
        }
    }
}
