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
    private Map<String,TimerThread> writeRetransmissionTimers;
    private TimerThread removeRetransmissionTimer;
    private boolean stopped;
    private List<RemoveMessage> myRemoveMessages;
    private List<RemoveMessage> othersRemoveMessages;
    private Object lock;

    public Logic(Server server, int serverNumber){
        this.lock = new Object();
        this.serverNumber=serverNumber;
        this.server=server;
        //this.executor = Executors.newCachedThreadPool();   //executor.submit(this);
        this.writeBuffer = new LinkedList<WriteMessage>();
        this.ackBuffer = new LinkedList<AckMessage>();
        this.queue = new ArrayList<Message>();
        this.writeRetransmissionTimers = new HashMap<String, TimerThread>();
        this.vectorClock = new ArrayList<Integer>();
        this.performedWrites = new ArrayList<WriteMessage>();
        this.ackRemovedServers = new ArrayList<AckRemovedServer>();//ack del MIO removeMessage
        this.stopped = false;
        this.myRemoveMessages = new ArrayList<RemoveMessage>();//quelle inviate da me
        this.othersRemoveMessages = new ArrayList<RemoveMessage>();//quelle degli altri per capire se io le ho già fatte (o le sto facendo)
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

    //only one server at a time can fail
    public void removeServer(RemoveMessage message){
        synchronized (this) {
            System.out.println("CALLED removeServer() FUNCTION");
            //entro qui se ho già ricevuto le remove di altri: sendAck e return
            for (int i = 0; i < this.othersRemoveMessages.size(); i++) {
                if (message.getTimeStamp() == othersRemoveMessages.get(i).getTimeStamp() &&
                        message.getServerNumber() == othersRemoveMessages.get(i).getServerNumber()) {
                    message.sendAckRemove(this);
                    return;
                }
            }
            //qua se mi ha già stoppato qualcuno (non quello di questo messaggio) o mi sono stoppato io
            if (isStopped()){
                othersRemoveMessages.add(message);
                message.sendAckRemove(this);
                return;
            }
            //se è il mio stesso messaggio che ho già inviato
            for (int i = 0; i < this.myRemoveMessages.size(); i++) {
                if (message.getTimeStamp() == myRemoveMessages.get(i).getTimeStamp() &&
                        message.getServerNumber() == myRemoveMessages.get(i).getServerNumber()) {
                    return;
                }
            }
            //se è la prima volta, avviata da me o da qualcun altro
            this.stopped = true;
            message.sendAckRemove(this);
            RemoveMessage removeMessage = new RemoveMessage(this.serverNumber,serverNumber);
            this.myRemoveMessages.add(removeMessage);
            server.sendMulti(removeMessage);
        }
    }

    public void checkAckRemove(RemoveMessage removeMessage){
        synchronized (this) {
            int count=0;
            for (int i = 0; i < ackRemovedServers.size(); i++) {
                if (removeMessage.getTimeStamp() == getMyRemoveMessages().get(i).getTimeStamp()){
                    count++;
                }
            }
            if (count >= (vectorClock.size()-1) ){
                if (!removeRetransmissionTimer.isInterrupted())
                    removeRetransmissionTimer.interrupt();
                if (this.serverNumber > removeMessage.getRemovedServerNumber())
                    this.serverNumber -= 1;
                vectorClock.remove(removeMessage.getRemovedServerNumber());//Removes the element at the specified position in this list. Shifts any subsequent elements to the left (subtracts one from their indices).
                this.ackBuffer.clear();
                this.queue.clear();
                String key;
                for (int i = 0; i < writeBuffer.size(); i++) {
                    key = String.valueOf(writeBuffer.get(i).getTimeStamp()).concat(String.valueOf(writeBuffer.get(i).getServerNumber()));
                    writeBuffer.get(i).getVectorClock().remove(removeMessage.getRemovedServerNumber());
                    writeRetransmissionTimers.get(key).getMessageToResend().getVectorClock().remove(removeMessage.getRemovedServerNumber());
                }
                // ripartono da sole le write
                this.getAckRemovedServers().clear();
                this.getMyRemoveMessages().clear();
                this.getOthersRemoveMessages().clear();
                for (int i = 0; i < getWriteBuffer().size(); i++) {
                    getWriteBuffer().get(i).getVectorClock().remove(removeMessage.getRemovedServerNumber());
                }
                this.stopped=false;
            }else
                return;
        }
    }

    public void write(String dataId, int newData) {
        WriteMessage message = new WriteMessage(this.serverNumber);
        message.fill(dataId,newData);
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
        synchronized (lock) {
            for (int i = 0; i < queue.size(); i++) {
                /*if (message.getServerNumber()==queue.get(i).getServerNumber())
                    continue;*///questo è solo per velocizzare la funzione
                if (!VectoUtil.outOfSequence(queue.get(i).getVectorClock(), this.vectorClock, queue.get(i).getServerNumber())) {
                    System.out.println("execution of a no-more-outOfSequence packet");
                    queue.get(i).execute(this);
                    queue.remove(i);//todo o sincronizzo o metto prima dell'execute?: è sincronizzata
                }
            }
            /*long index[] = new long[2];
            //index[0] -> serverNumber        index[1] -> timestamp
            index[0]=message.serverNumber;
            index[1]=message.timestamp;
            if (queue.containsKey(index))
                queue.get(index).execute(this);    */
        }
    }


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

    private synchronized void performWrite(WriteMessage writeMessage){
        try {
            String key = String.valueOf(writeMessage.getTimeStamp()).concat(String.valueOf(writeMessage.getServerNumber()));
            if (!writeRetransmissionTimers.get(key).isInterrupted()){
                writeRetransmissionTimers.get(key).interrupt();
                writeRetransmissionTimers.remove(key);
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

    public void setVectorClock(ArrayList<Integer> vectorClock) {
        this.vectorClock = vectorClock;
        System.out.println("NEW VECTORCLOCK: " + this.vectorClock );
    }

    public boolean isStopped() {        return stopped;    }

    public LinkedList<WriteMessage> getWriteBuffer() {        return writeBuffer;    }

    public LinkedList<AckMessage> getAckBuffer() {        return ackBuffer;    }

    public List<Message> getQueue() {        return queue;    }

    public int getServerNumber() {        return serverNumber;    }

    public void setServerNumber(int serverNumber) {        this.serverNumber = serverNumber;    }

    public List<AckMessage> getTransmittedAcks() {        return transmittedAcks;    }

    public List<RemoveMessage> getOthersRemoveMessages() {
        return othersRemoveMessages;
    }

    public List<RemoveMessage> getMyRemoveMessages() {        return myRemoveMessages;     }


    public TimerThread getRemoveRetransmissionTimer() {
        return removeRetransmissionTimer;
    }

    public void setRemoveRetransmissionTimer(TimerThread removeRetransmissionTimer) {
        this.removeRetransmissionTimer = removeRetransmissionTimer;
    }

    public Map<String, TimerThread> getWriteRetransmissionTimers() {
        return writeRetransmissionTimers;
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

