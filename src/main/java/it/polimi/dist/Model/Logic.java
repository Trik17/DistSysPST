package it.polimi.dist.Model;

import it.polimi.dist.Server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Logic{

    protected Server server;
    //protected Map<Integer,Message> messages;
    protected ArrayList<Integer> vectorClock;
    protected int serverNumber;
    //private ExecutorService executor; //executor.submit(this);
    protected LinkedList<WriteMessage> writeBuffer;
    protected LinkedList<WriteMessage> resendBuffer;
    protected LinkedList<Acknowledgement> ackBuffer;
    private Map<int[],Message> queue;

    public Logic(Server server, int serverNumber){
        this.serverNumber=serverNumber;
        this.server=server;
        //this.messages = new HashMap<Integer,Message>();
        //this.executor = Executors.newCachedThreadPool();
        this.writeBuffer = new LinkedList<WriteMessage>();
        this.resendBuffer = new LinkedList<WriteMessage>();
        this.ackBuffer = new LinkedList<Acknowledgement>();
        this.queue = new HashMap<int[], Message>();
        this.vectorClock = new ArrayList<Integer>();
    }

    //TODO -> collegare a server santa
    public void inizializeVector(){
        //TODO
    }

    //TODO -> collegare a server santa
    public void inizializeData(){
        //todo
    }

    //TODO -> collegare a server santa
    public void addServer(){
        //TODO
        //e le write già pending??
        //e per questo bisogna mandare qualche conferma!!!

    }

    //TODO -> collegare a server santa
    public void removeServer(int serverNumber){
        for (int i = 0; i < writeBuffer.size(); i++) {
            if(writeBuffer.get(i).serverNumber==serverNumber)
                writeBuffer.remove(i);
        }
        for (int j = 0; j < ackBuffer.size(); j++) {
            if(ackBuffer.get(j).serverNumber==serverNumber)
                ackBuffer.remove(j);
        }
    }

    public void write(String dataId, int newData) {
        WriteMessage message = new WriteMessage(this.serverNumber);
        message.fill(dataId,newData);
        writeBuffer.add(message);
        message.setVectorClock(VectoClockUtil.addOne(this));
        server.sendMulti(message);
    }

    public void receive(Message message){
        if(serverNumber==-1)
            if (message.isNetMessage)
                message.execute(this);
                else
                    return;
        if(!message.isNetMessage && VectoClockUtil.outOfSequence(message.vectorClock,this.vectorClock, message.serverNumber)) {
            int index[] = new int[2];
            index = VectoClockUtil.missedMessage(message.vectorClock,this.vectorClock);
            queue.put(index,message);
            //todo requestRetransmission(i);//ma deve aspettare un attimo magari?
            return;
        }
        message.execute(this);
        //todo if(filledMessage()){        }
    }

    private void requestRetransmission(int i) {
        //RequestRetransmission r = new RequestRetransmission();
        //r.fill();
        //TODO fare un messaggio particolare che chieda la ritrasmissione: e un metodo che lo ritrasmette
        //server.sendMulti(r);
    }

    public void checkAckBuffer(){
        int count=0;
        for (int i = 0; i < writeBuffer.size(); i++) {
            for (int j = 0; j < ackBuffer.size(); j++) {
                if (ackBuffer.get(j).writeTimestamp==writeBuffer.get(i).timestamp
                        && ackBuffer.get(j).writeServerNumber==writeBuffer.get(i).serverNumber)
                    count++;
            }
            if (count>= vectorClock.size())
                performWrite(writeBuffer.get(i));
        }
    }

    private void performWrite(WriteMessage writeMessage){
        server.getStorage().write(writeMessage.key,writeMessage.data);
    }

    public Server getServer() {   return server;    }

    public ArrayList<Integer> getVectorClock() {
        return vectorClock;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }
}
/*
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
are receive:
– ts(r)[j] = Vk[j]+1
– ts(r)[i] ≤ Vk[i] for all i ≠ j

TODO 1: il rinvio di uno perso
todo 4: inizializzazione del vector clock
todo alla connessione invirsi le pending, data storage e vector clock
-------------------
TODO 2: server che cadono e devono riavviarsi e sicronizzare i dati
        (e se cadono devo toglierli dai vectorclock?)
TODO 3: server sconosciuti, va bene quel che abbiamo fatto?

TODO se nella read chiedo un elemento che non esiste : restituire qualcosa es.0 o -1 o un'allerta
 */

