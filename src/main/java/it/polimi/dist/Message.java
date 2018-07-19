package it.polimi.dist;

import java.util.ArrayList;

public abstract class Message {

    protected int timestamp;
    protected int processNumber;
    protected String key;
    protected int data;
    protected ArrayList<Integer> lamport;
    protected int ID;//serve?

    public Message() {
    }

    public void execute(Server server) {
        //called by server in order  to get and use data and set timestamp/processNumber
    }

    public void fill(String key, int data) {
        //filled by Client (set key/data)
        this.data=data;
        this.key=key;
    }

    public void setLamport(ArrayList lamport) {
        this.lamport = lamport;
    }

    public ArrayList<Integer> getLamport() {
        return lamport;
    }

    public int getTimeStamp() {
        return timestamp;
    }

    public int getID() {
        return ID;
    }

    public int getProcessNumber() {
        return processNumber;
    }
}
