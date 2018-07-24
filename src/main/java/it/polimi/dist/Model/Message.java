package it.polimi.dist.Model;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Message implements Serializable {

    protected long timestamp;
    protected String key;
    protected int data;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber; //position in the vector clock of the message's server
    protected boolean isNetMessage;

    public Message(int serverNumber){
        this.serverNumber = serverNumber;
        this.timestamp = System.currentTimeMillis();
        this.isNetMessage=false;
    }

    public abstract void execute(Logic logic);

    public void fill(String key, int data) {
        //filled by Client (set key/data)
        this.data = data;
        this.key = key;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }


    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }


    @Override
    public boolean equals(Object obj) {
        if (((Message)obj).timestamp==this.timestamp
                && ((Message)obj).serverNumber==this.serverNumber)
            return true;
        else
            return false;
    }
}
