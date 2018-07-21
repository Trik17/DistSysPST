package it.polimi.dist.Messages;

import it.polimi.dist.Logic;
import it.polimi.dist.Server;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public abstract class Message implements Serializable {

    protected long timestamp;
    /*
    the timestamp is usefull
     */
    protected String key;
    protected int data;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber; //position in the vector clock of the message's server

    public Message(int serverNumber){
        this.serverNumber=serverNumber;
        this.timestamp = System.currentTimeMillis();
    }

    public abstract void execute(Logic logic);

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
