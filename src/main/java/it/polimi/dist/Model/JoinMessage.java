package it.polimi.dist.Model;

import it.polimi.dist.Server;

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

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        timerThread.start();
        // add to Hash Map in Logic with Message - Timer
    }
}
