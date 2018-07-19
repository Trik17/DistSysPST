package it.polimi.dist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logic implements Runnable{

    private int sequenceID;
    private Server server;
    private Map<Integer,Message> messages;
    private ArrayList<Integer> lamport;
    private ExecutorService executor; //executor.submit(this);
    private LinkedList<WriteMessage> writeBuffer;

    //TODO server i chi manda i messsaggi! per fare le richieste di ritrasmissione

    public Logic(int sequenceID, Server server){
        this.server=server;
        this.sequenceID = sequenceID;
        this.messages = new HashMap();
        this.executor = Executors.newCachedThreadPool();
        this.writeBuffer = new LinkedList<WriteMessage>();
    }

    public void write(String dataId, int newData) {
        //mettere un buffer delle richieste per write
        /*TODO manda broadcast a tutti e aspetta gli ack
        TODO per mettere un ordine: timestamp e poi numero del server
         */
        WriteMessage m = new WriteMessage();
        m.fill(dataId,newData);
        writeBuffer.add(m);
        executor.submit(this);
    }

    //TODO
    public void received(Message m){
        if (m.getID()<sequenceID)
            return;
        if (m.getID()!=sequenceID+1)
            outOfSequence(m.getID());

    }

    private void outOfSequence(int id) {
        for (int i = id; i < id; i++) {
            requestRetransmission(i);
        }
    }

    //TODO fare un messaggio particolare che chieda la ritrasmissione: e un metodo che lo ritrasmette

    //TODO
    private void requestRetransmission(int i) {

    }

    public void run() {
        // dopo che verifica che può deve scrivere il messaggio che ha in testa alla lista
        //this.server.getData().write(dataId,newData);
    }

    public void setMessages(Map<Integer, Message> messages) {
        this.messages = messages;
    }

    public Message getMessage(int id) {
        return this.messages.get(id);
    }
}
