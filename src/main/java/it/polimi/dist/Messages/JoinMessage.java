package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.TimerThread;
import it.polimi.dist.ServerPackage.Server;

import java.util.Random;

public class JoinMessage extends Message {

    private long random;

    public JoinMessage() {
        super(-1);
        Random rnd = new Random();
        this.random = rnd.nextLong();
        this.isNetMessage = true;
    }

    @Override
    public void execute(Logic logic) {
        if (logic.getServerNumber() != -1) {
            int serverNumber = logic.getServerNumber();
            int numberOfServers = logic.getVectorClock().size();
            AckJoinMessage joinMessage = new AckJoinMessage(serverNumber, numberOfServers, logic.getServer().getStorage());
            logic.getServer().sendMulti(joinMessage);
            if (!logic.getServer().getJoinHandler().getRandoms().contains(random)) {
                logic.addServer();
                logic.getServer().getJoinHandler().getRandoms().add(random);
            }
        }
    }

    public long getRandom() {
        return random;
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
