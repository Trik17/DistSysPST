package it.polimi.dist;

public class Acknowledgement extends Message {

    public Acknowledgement(Server server){
        this.processNumber = server.getProcessNumber();
        this.timestamp = server.getLamportClock();
    }
}
