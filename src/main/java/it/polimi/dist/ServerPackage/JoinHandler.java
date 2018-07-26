package it.polimi.dist.ServerPackage;

import it.polimi.dist.Messages.AckJoinMessage;
import it.polimi.dist.ServerPackage.Server;
import it.polimi.dist.ServerPackage.TimerThread;

import java.util.ArrayList;
import java.util.List;

public class JoinHandler {

    private Server server;
    private List<AckJoinMessage> ackJoinBuffer;
    private TimerThread timerJoin;
    private List<Long> randoms;

    public JoinHandler(Server server) {
        this.server = server;
        this.ackJoinBuffer = new ArrayList<AckJoinMessage>();
        this.randoms = new ArrayList<Long>();
    }

    public List<Long> getRandoms() {
        return randoms;
    }

    public List<AckJoinMessage> getAckJoinBuffer() {
        return ackJoinBuffer;
    }

    public void setTimerJoin(TimerThread timerJoin) {
        this.timerJoin = timerJoin;
    }

    public void checkTimer() {
        if (ackJoinBuffer.size() >= ackJoinBuffer.get(0).getNumberOfServers() )
            if (!timerJoin.isInterrupted())
                timerJoin.interrupt();
    }
}
