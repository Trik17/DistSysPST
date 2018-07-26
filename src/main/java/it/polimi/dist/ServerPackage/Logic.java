package it.polimi.dist.ServerPackage;


import it.polimi.dist.Messages.*;
import java.util.*;

public class Logic{

    private Server server;
    private ArrayList<Integer> vectorClock;
    private int serverNumber;
    //private ExecutorService executor;
    private LinkedList<WriteMessage> writeBuffer;
    private List<WriteMessage> performedWrites;
    private List<AckMessage> transmittedAcks;
    //private LinkedList<WriteMessage> resendBuffer;
    private LinkedList<AckMessage> ackBuffer;
    private List<AckRemovedServer> ackRemovedServers;
    private List<Message> queue;
    private Map<String,TimerThread> retransmissionTimers;
    private boolean stopped;
    private List<RemoveMessage> removeMessages;

    public Logic(Server server, int serverNumber){
        this.serverNumber=serverNumber;
        this.server=server;
        //this.executor = Executors.newCachedThreadPool();   //executor.submit(this);
        this.writeBuffer = new LinkedList<WriteMessage>();
        this.ackBuffer = new LinkedList<AckMessage>();
        this.queue = new ArrayList<Message>();
        this.retransmissionTimers = new HashMap<String, TimerThread>();
        this.vectorClock = new ArrayList<Integer>();
        this.performedWrites = new ArrayList<WriteMessage>();
        this.ackRemovedServers = new ArrayList<AckRemovedServer>();//ack del MIO removeMessage
        this.stopped = false;
        this.removeMessages = new ArrayList<RemoveMessage>();//quelle degli altri (e anche il mio che mi mando da solo) per capire se io le ho già fatte (o le sto facendo)
        this.transmittedAcks = new ArrayList<AckMessage>();
        if(serverNumber==-1)
            initializeVectorClock(1);
        else
            initializeVectorClock(serverNumber+1);
    }

    public void initializeVectorClock(int size){
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

    public void removeServer(RemoveMessage message){
        synchronized (this) {
            for (int i = 0; i < this.removeMessages.size(); i++) {
                if (message.getTimeStamp() == removeMessages.get(i).getTimeStamp() &&
                        message.getServerNumber() == removeMessages.get(i).getServerNumber())
                    message.sendAckRemove(this);
                    break;
            }
            if (isStopped()) {

                return;
            }
            this.stopped = true;//todo + poi toglierlo + blocco gli invii ??
            RemoveMessage removeMessage = new RemoveMessage(this.serverNumber,serverNumber);
            this.removeMessages.add(removeMessage);
            server.sendMulti(removeMessage);
        }
    }
    //todo
    public void checkAckRemove(int removedServer){
        synchronized (this) {
            if (ackRemovedServers.size() >= (vectorClock.size()-1) ){
                if (this.serverNumber>removedServer)//qua o dopo gli ack?
                    this.serverNumber-=1;
                vectorClock.remove(removedServer);//Removes the element at the specified position in this list. Shifts any subsequent elements to the left (subtracts one from their indices).
                this.ackBuffer.clear();
                this.queue.clear();
                //todo stoppare timer del removingMessage
                for (int i = 0; i < ; i++) {

                }
                //todo ripartono da sole le write?
                //todo aggiornare vector clock dei messaggi in write buffer
                //todo e quelli nei timer? vanno risettati anche a loro i clock!
                // todo togliere il server caduto dal vector clock e ricontrollo che ora mi bastino gli ack
                /*
                e se mi arriva poi una remove server di ritrasmissione
                dopo che io ho finito e sono ripartito?
                 */
                this.stopped=false;
            }else
                return;
        }
    }

    public void write(String dataId, int newData) {
        WriteMessage message = new WriteMessage(this.serverNumber);
        message.fill(dataId,newData);
        //writeBuffer.add(message);
        message.setVectorClock(VectoUtil.addOne(this, this.serverNumber));
        server.sendMulti(message);
    }

    public void receive(Message message){
        if(serverNumber == -1) {
            if (message.isNetMessage())
                message.execute(this);
            else
                return;
        }
        if (isStopped()){
            if (message.isRemovingMessage())
                message.execute(this);
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
                queue.remove(i);
            }
        }
    }

    public void checkAckBuffer(){
        synchronized (this) {
            //controlla quanti ack ci sono e se sono > di vectorclock size fa la scrittura
            int count = 0;
            for (WriteMessage aWriteBuffer : writeBuffer) {
                for (AckMessage anAckBuffer : ackBuffer) {
                    if (anAckBuffer.getWriteTimestamp() == aWriteBuffer.getTimeStamp()
                            && anAckBuffer.getWriteServerNumber() == aWriteBuffer.getServerNumber())
                        count++;
                }
                if (count >= vectorClock.size())
                    performWrite(aWriteBuffer);
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

    public List<AckRemovedServer> getAckRemovedServers() { return ackRemovedServers;     }

    public Server getServer() {   return server;    }

    public List<WriteMessage> getPerformedWrites() { return performedWrites;    }

    public ArrayList<Integer> getVectorClock() {        return vectorClock;    }

    public void setVectorClock(ArrayList<Integer> vectorClock) {   this.vectorClock = vectorClock;    }

    public boolean isStopped() {        return stopped;    }

    public LinkedList<WriteMessage> getWriteBuffer() {        return writeBuffer;    }

    public LinkedList<AckMessage> getAckBuffer() {        return ackBuffer;    }

    public List<Message> getQueue() {        return queue;    }

    public Map<String, TimerThread> getRetransmissionTimers() {        return retransmissionTimers;    }

    public int getServerNumber() {        return serverNumber;    }

    public void setServerNumber(int serverNumber) {        this.serverNumber = serverNumber;    }

    public List<AckMessage> getTransmittedAcks() {        return transmittedAcks;    }

    public List<RemoveMessage> getRemoveMessages() {        return removeMessages;     }
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

