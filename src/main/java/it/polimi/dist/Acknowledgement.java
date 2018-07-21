package it.polimi.dist;

import it.polimi.dist.Messages.Message;

public class Acknowledgement extends Message {

    public Acknowledgement(Server server){
        this.serverNumber = server.getProcessNumber();
        this.timestamp = server.getLamportClock();
    }
}
