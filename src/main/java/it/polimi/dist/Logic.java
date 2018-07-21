package it.polimi.dist;

import it.polimi.dist.Messages.Message;
import it.polimi.dist.Messages.RequestRetransmission;
import it.polimi.dist.Messages.WriteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logic{

    private int serverID;
    private Server server;
    private Map<Integer,Message> messages;
    private ArrayList<Integer> vectorClock;
    private ExecutorService executor; //executor.submit(this);
    private LinkedList<WriteMessage> writeBuffer;
    private LinkedList<WriteMessage> resendBuffer;
    private LinkedList<Acknowledgement> ackBuffer;

    //TODO server i chi manda i messsaggi! per fare le richieste di ritrasmissione

    public Logic(int serverID, Server server){
        this.server=server;
        this.serverID = serverID;
        this.messages = new HashMap();
        this.executor = Executors.newCachedThreadPool();
        this.writeBuffer = new LinkedList<WriteMessage>();
        this.resendBuffer = new LinkedList<WriteMessage>();
        this.ackBuffer = new LinkedList<Acknowledgement>();
    }

    public void write(String dataId, int newData) {
        /*TODO manda broadcast a tutti e aspetta gli ack
        */
        WriteMessage m = new WriteMessage();
        m.fill(dataId,newData);
        writeBuffer.add(m);
        SendWrite send = new SendWrite(this, server);
        executor.submit(send);
    }

    //TODO sia per messaggi di scrittura che per gli acks
    public void received(Message m){
        if (m.getID()< serverID)
            return;
        if (m.getID()!= serverID +1)
            outOfSequence(m.getID());
    }

    //TODO prima aspetto un certo tempo
    private void outOfSequence(int id) {
        for (int i = id; i < id; i++) {
            requestRetransmission(i);
        }
    }

    //TODO fare un messaggio particolare che chieda la ritrasmissione: e un metodo che lo ritrasmette
    private void requestRetransmission(int i) {
        RequestRetransmission r = new RequestRetransmission();
        //r.fill();
        server.sendMulti(r);
    }

    public LinkedList<WriteMessage> getResendBuffer() {  return resendBuffer;    }

    public LinkedList<WriteMessage> getWriteBuffer() {   return writeBuffer;    }

    public void setMessages(Map<Integer, Message> messages) { this.messages = messages;   }

    public Message getMessage(int id) {    return this.messages.get(id);    }
}
/*
TODO:
ogni server parte da 0
ogni volta che si aggiunge un server aggiungo un nuovo elemento al vectorClock
ad ogni messaggio inviato (ad ogni write inserita dall'utente
        & ad ogni risposta di ack [ incremento di solo 1 per tutti gli ack)
         aumento di uno il numero del server

Using vector clocks:
• Variation: Increment clock only when
sending a message. On receive, just merge,
not increment
• Hold a reply until the previous messages
are received:
– ts(r)[j] = Vk[j]+1
– ts(r)[i] ≤ Vk[i] for all i ≠ j





 */
