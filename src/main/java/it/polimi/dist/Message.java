package it.polimi.dist;

public abstract class Message {
    protected int timestamp;
    protected int processNumber;
    protected String key;
    protected int data;

    public Message() {
    }

    public void execute() {
        //called by server in order  to get and use data and set timestamp/processNumber
    }

    public void fill() {
        //filled by Client (set key/data)
    }
}
