package it.polimi.dist;

public abstract class Message {
    protected int ID;

    protected int timestamp;
    protected int processNumber;
    protected String key;
    protected int data;

    public Message() {
    }

    public void execute(Server server) {
        //called by server in order  to get and use data and set timestamp/processNumber
    }

    public void fill() {
        //filled by Client (set key/data)
    }

    public int getTimeStamp() {
        return timestamp;
    }

    public int getID() {
        return ID;
    }
}
