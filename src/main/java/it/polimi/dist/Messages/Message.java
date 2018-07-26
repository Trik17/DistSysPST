package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.Server;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Message implements Serializable {

    protected long timestamp;
    protected String key;
    protected int data;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber; //position in the vector clock of the message's server
    protected boolean isNetMessage;
    protected int numberOfRetransmission = 0;

    public Message(int serverNumber){
        this.serverNumber = serverNumber;
        this.timestamp = System.currentTimeMillis();
        this.isNetMessage=false;
        this.vectorClock = new ArrayList<Integer>();
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

    public boolean isNetMessage() {
        return isNetMessage;
    }

    public int getServerNumber() {
        return serverNumber;
    }


    public String getKey() {
        return key;
    }

    public int getData() {
        return data;
    }

    public void retransmission(Server server){
        return;
    }

    public int getNumberOfRetransmission() {
        return numberOfRetransmission;
    }

    public void setNumberOfRetransmission(int numberOfRetransmission) {
        this.numberOfRetransmission = numberOfRetransmission;
    }

    @Override
    public boolean equals(Object obj) {
        if (((Message)obj).timestamp == this.timestamp
                && ((Message)obj).serverNumber == this.serverNumber)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "\nTimestamp: " + String.valueOf(timestamp) + "\nServer Number: " + String.valueOf(serverNumber)
                + "Vector Clock: " + arrayToString(vectorClock) + "\n------------------------------";
    }

    private String arrayToString(ArrayList<Integer> vectorClock) {
        String string = "";
        for (int i = 0; i < vectorClock.size(); i++){
            string = string.concat(String.valueOf(vectorClock.get(i)));
        }
        return  string;
    }
}
