package it.polimi.dist.Messages;

import it.polimi.dist.Server;
import java.sql.Timestamp;
import java.util.ArrayList;

public abstract class Message {

    protected long timestamp; //needed?
    protected String key;
    protected int data;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber; //position in the vector clock of the message's server

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(int serverNumber){
        this.serverNumber=serverNumber;
        this.timestamp = System.currentTimeMillis();
    }

    public void execute(Server server) {
        //called by server in order  to get and use data and set timestamp/serverNumber
    }

    public void fill(String key, int data) {
        //filled by Client (set key/data)
        this.data=data;
        this.key=key;
    }

    public void setVectorClock(ArrayList vectorClock) {
        this.vectorClock = vectorClock;
    }

    public ArrayList<Integer> getVectorClock() {
        return vectorClock;
    }

    public long getTimeStamp() {
        return timestamp;
    }

    public int getServerNumber() {
        return serverNumber;
    }
}
