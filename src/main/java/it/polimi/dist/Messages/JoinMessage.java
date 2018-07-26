package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.TimerThread;
import it.polimi.dist.ServerPackage.Server;

public class JoinMessage extends Message {


    public JoinMessage() {
        super(-1);
        this.isNetMessage = true;
    }

    @Override
    public void execute(Logic logic) {
        if (logic.getServerNumber() != -1) { //not done by the new server joined
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
        server.getJoinHandler().setTimerJoin(timerThread);
        timerThread.start();
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nJOIN MESSAGE" + super.toString();
    }
}
