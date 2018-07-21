package it.polimi.dist;

import it.polimi.dist.Messages.Message;
//import it.polimi.dist.Messages.RequestRetransmission;
import it.polimi.dist.Messages.WriteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logic{

    protected Server server;
    protected Map<Integer,Message> messages;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber;
    private ExecutorService executor; //executor.submit(this);
    protected LinkedList<WriteMessage> writeBuffer;
    protected LinkedList<WriteMessage> resendBuffer;
    //protected LinkedList<Acknowledgement> ackBuffer;

    public Logic(Server server, int serverNumber){
        this.serverNumber=serverNumber;
        this.server=server;
        this.messages = new HashMap<Integer,Message>();
        this.executor = Executors.newCachedThreadPool();
        this.writeBuffer = new LinkedList<WriteMessage>();
        this.resendBuffer = new LinkedList<WriteMessage>();
        //this.ackBuffer = new LinkedList<Acknowledgement>();
    }

    public void write(String dataId, int newData) {
        /*TODO manda broadcast a tutti e aspetta gli ack
        */
        WriteMessage m = new WriteMessage(this.serverNumber);
        m.fill(dataId,newData);
        writeBuffer.add(m);
        SendWrite send = new SendWrite(this, server, m);
        executor.submit(send);
        //forse si può mettere tutto qua il codice della SENDWRITE TODO
    }

    //TODO sia per messaggi di scrittura che per gli acks
    public void received(Message m){
        //if (m.getID()< serverID)
          //  return;
        //if (m.getID()!= serverID +1)
         //   outOfSequence(m.getID());
    }

    //TODO prima aspetto un certo tempo
    private void outOfSequence(int id) {
        for (int i = id; i < id; i++) {
            requestRetransmission(i);
        }
    }

    //TODO fare un messaggio particolare che chieda la ritrasmissione: e un metodo che lo ritrasmette
    private void requestRetransmission(int i) {
        //RequestRetransmission r = new RequestRetransmission();
        //r.fill();
        //server.sendMulti(r);
    }


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
