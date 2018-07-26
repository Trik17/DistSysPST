package it.polimi.dist.ServerPackage;

import it.polimi.dist.Messages.Acknowledgement;
import it.polimi.dist.Messages.Message;
import it.polimi.dist.Messages.WriteMessage;
import java.util.*;

public class Logic{

    private Server server;
    //private Map<Integer,Message> messages;
    private ArrayList<Integer> vectorClock;
    private int serverNumber;
    //private ExecutorService executor; //executor.submit(this);
    private LinkedList<WriteMessage> writeBuffer;
    private List<WriteMessage> performedWrites;
    private List<Acknowledgement> transmittedAcks;
    //private LinkedList<WriteMessage> resendBuffer;
    private LinkedList<Acknowledgement> ackBuffer;
    private List<Message> queue;
    private Map<String,TimerThread> retransmissionTimers;



    /*
    index[0] -> serverNumber
    index[1] -> timestamp
     */

    public Logic(Server server, int serverNumber){
        this.serverNumber=serverNumber;
        this.server=server;
        //this.messages = new HashMap<Integer,Message>();
        //this.executor = Executors.newCachedThreadPool();
        this.writeBuffer = new LinkedList<WriteMessage>();
        //this.resendBuffer = new LinkedList<WriteMessage>();
        this.ackBuffer = new LinkedList<Acknowledgement>();
        this.queue = new ArrayList<Message>();
        this.retransmissionTimers = new HashMap<String, TimerThread>();
        this.vectorClock = new ArrayList<Integer>();
        this.performedWrites = new ArrayList<WriteMessage>();
        this.transmittedAcks = new ArrayList<Acknowledgement>();

        if(serverNumber==-1)
            inizializeVectorClock(1);
        else
            inizializeVectorClock(serverNumber+1);
    }

    public void inizializeVectorClock(int size){
        for (int i = 0; i < size; i++) {
            vectorClock.add(0);
        }
    }

    public void addServer(){
        for (int i = 0; i < vectorClock.size(); i++) {
            vectorClock.set(i,0);
        }
        vectorClock.add(0);
        ackBuffer.clear();
        queue.clear();
        for (int i = 0; i < writeBuffer.size(); i++) {
            if (writeBuffer.get(i).getServerNumber()!=this.serverNumber)
                writeBuffer.remove(i);
        }
    }

    //TODO -> collegare a server santa
    public void removeServer(int serverNumber){

        /*
        for (int i = 0; i < writeBuffer.size(); i++) {
            if(writeBuffer.get(i).getServerNumber()==serverNumber)
                writeBuffer.remove(i);
        }*/
        //
        // todo o invece devo togliere quelle pending?
        // todo togliere il server caduto dal vector clock e ricontrollo che ora mi bastino gli ack
        /*
        for (int j = 0; j < ackBuffer.size(); j++) {
            if(ackBuffer.get(j).getServerNumber()==serverNumber)
                ackBuffer.remove(j);
        }*/
        synchronized (this) {
            if (this.serverNumber>serverNumber)
                this.serverNumber-=1;
            //e tutti i messaggi che hanno i clock e i server number sbagliati?
            //-> cancello tutti i pending e gli reimposto il clock
            //todo e quelli nei timer? vanno risettati anche a loro i clock!
            vectorClock.remove(serverNumber);
        }
        checkAckBuffer();
    //checkQueue(???); ???
}

    public void write(String dataId, int newData) {
        WriteMessage message = new WriteMessage(this.serverNumber);
        message.fill(dataId,newData);
        //writeBuffer.add(message);
        message.setVectorClock(VectoUtil.addOne(this, this.serverNumber));
        server.sendMulti(message);
    }

    public void receive(Message message){
        if(serverNumber==-1) {
            if (message.isNetMessage())
                message.execute(this);
            else
                return;
        }
        if(!message.isNetMessage() &&
                VectoUtil.outOfSequence(message.getVectorClock(),this.vectorClock, message.getServerNumber())){
            queue.add(message);
            return;
        }
        message.execute(this);
        checkQueue(message);
    }

    private void checkQueue(Message message) {
        for (int i = 0; i < queue.size(); i++) {
            /*if (message.getServerNumber()==queue.get(i).getServerNumber())
                continue;*///questo è solo per velocizzare la funzione
            if (!VectoUtil.outOfSequence(queue.get(i).getVectorClock(),this.vectorClock, queue.get(i).getServerNumber())){
                System.out.println("execution of a no-more-outOfSequence packet");
                queue.get(i).execute(this);
            }
        }
        /*long index[] = new long[2];
        //index[0] -> serverNumber        index[1] -> timestamp
        index[0]=message.serverNumber;
        index[1]=message.timestamp;
        if (queue.containsKey(index))
            queue.get(index).execute(this);
    */}

    public void checkAckBuffer(){
        synchronized (this) {
            //controlla quanti ack ci sono e se sono > di vectorclock size fa la scrittura
            int count = 0;
            for (int i = 0; i < writeBuffer.size(); i++) {
                for (int j = 0; j < ackBuffer.size(); j++) {
                    if (ackBuffer.get(j).getWriteTimestamp() == writeBuffer.get(i).getTimeStamp()
                            && ackBuffer.get(j).getWriteServerNumber() == writeBuffer.get(i).getServerNumber())
                        count++;
                }
                if (count >= vectorClock.size())
                    performWrite(writeBuffer.get(i));
                count = 0;
            }
        }
    }

    private void performWrite(WriteMessage writeMessage){
        try {
            String key = String.valueOf(writeMessage.getTimeStamp()).concat(String.valueOf(writeMessage.getServerNumber()));
            if (!retransmissionTimers.get(key).isInterrupted()){
                retransmissionTimers.get(key).interrupt();
                retransmissionTimers.remove(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        server.getStorage().write(writeMessage.getKey(),writeMessage.getData());
        this.performedWrites.add(writeMessage);
        writeBuffer.remove(writeMessage);
        for (int j = 0; j < ackBuffer.size(); j++) {
            if (ackBuffer.get(j).getWriteTimestamp()==writeMessage.getTimeStamp()
                    && ackBuffer.get(j).getWriteServerNumber()==writeMessage.getServerNumber())
                ackBuffer.remove(j);
        }
        System.out.println("Write performed: id = " + writeMessage.getKey() + "; value = " + String.valueOf(writeMessage.getData()));
    }

    public Server getServer() {   return server;    }

    public List<WriteMessage> getPerformedWrites() { return performedWrites;    }

    public ArrayList<Integer> getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(ArrayList<Integer> vectorClock) {
        this.vectorClock = vectorClock;
    }

    public LinkedList<WriteMessage> getWriteBuffer() {
        return writeBuffer;
    }

    public LinkedList<Acknowledgement> getAckBuffer() {
        return ackBuffer;
    }

    public List<Message> getQueue() {
        return queue;
    }

    public Map<String, TimerThread> getRetransmissionTimers() {
        return retransmissionTimers;
    }

    public int getServerNumber() {
        return serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }

    public List<Acknowledgement> getTransmittedAcks() {
        return transmittedAcks;
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
todo alla connessione invirsi le pending, data storage e vector clock
-------------------
TODO 2: server che cadono e devono riavviarsi e sicronizzare i dati
        (e se cadono devo toglierli dai vectorclock?)
TODO 3: server sconosciuti, va bene quel che abbiamo fatto?
TODO timer di ritrasmissione

todo cosa succede se arriva 2 volte lo stesso messaggio? devo rispondere ma senza aumentare il clock

- non si riaggiungano quelli già presenti!
*/

