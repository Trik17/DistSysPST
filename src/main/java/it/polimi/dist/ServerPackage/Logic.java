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
    private Map<ArrayList<Long>,Message> queue;
    private Map<Message,TimerThread> retransmissionTimers;



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
        this.queue = new HashMap<ArrayList<Long>, Message>();
        this.retransmissionTimers = new HashMap<Message, TimerThread>();
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

        //TODO aspettare qualche secondo e iniziare a inviare di nuovo le write vecchie
    }

    //TODO -> collegare a server santa
    public void removeServer(int serverNumber){
        for (int i = 0; i < writeBuffer.size(); i++) {
            if(writeBuffer.get(i).getServerNumber()==serverNumber)
                writeBuffer.remove(i);
        }
        for (int j = 0; j < ackBuffer.size(); j++) {
            if(ackBuffer.get(j).getServerNumber()==serverNumber)
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
        if(serverNumber==-1) {
            if (message.isNetMessage())
                message.execute(this);
            else
                return;
        }
        /*if(!message.isNetMessage() && VectoClockUtil.outOfSequence(message.getVectorClock(),this.vectorClock, message.getServerNumber())) {
            //TODO NON FUNZIONA!!!
            /*
            a
            a
            a
            a
            a
            a  QUEUEEEEEEEEEE
            a
            a
            a

             *//*
            ArrayList<Long> index ;
            index=VectoClockUtil.missedMessage(message.getVectorClock(),this.vectorClock);
            queue.put(index,message);
            //todo requestRetransmission(i);//ma deve aspettare un attimo magari?
            return;
        }*/
        message.execute(this);
        checkQueue(message);
    }

    private void checkQueue(Message message) {
        //todo  forse è più di uno? devo fare una specie di for??
        /*long index[] = new long[2];
        //index[0] -> serverNumber        index[1] -> timestamp
        index[0]=message.serverNumber;
        index[1]=message.timestamp;
        if (queue.containsKey(index))
            queue.get(index).execute(this);
    */}

    public void checkAckBuffer(){
        //controlla quanti ack ci sono e se sono > di vectorclock size fa la scrittura
        int count=0;
        for (int i = 0; i < writeBuffer.size(); i++) {
            for (int j = 0; j < ackBuffer.size(); j++) {
                if (ackBuffer.get(j).getWriteTimestamp()==writeBuffer.get(i).getTimeStamp()
                        && ackBuffer.get(j).getWriteServerNumber()==writeBuffer.get(i).getServerNumber())
                    count++;
            }
            if (count>= vectorClock.size())
                performWrite(writeBuffer.get(i));
            count=0;
        }
    }

    private void performWrite(WriteMessage writeMessage){
        retransmissionTimers.get(writeMessage).interrupt();
        retransmissionTimers.remove(writeMessage);
        server.getStorage().write(writeMessage.getKey(),writeMessage.getData());
        this.performedWrites.add(writeMessage);
        writeBuffer.remove(writeMessage);
        for (int j = 0; j < ackBuffer.size(); j++) {
            if (ackBuffer.get(j).getWriteTimestamp()==writeMessage.getTimeStamp()
                    && ackBuffer.get(j).getWriteServerNumber()==writeMessage.getServerNumber())
                ackBuffer.remove(j);
        }
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

    public Map<ArrayList<Long>, Message> getQueue() {
        return queue;
    }

    public Map<Message, TimerThread> getRetransmissionTimers() {
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

