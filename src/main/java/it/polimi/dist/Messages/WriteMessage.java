package it.polimi.dist.Messages;

import it.polimi.dist.ServerPackage.Logic;
import it.polimi.dist.ServerPackage.TimerThread;
import it.polimi.dist.ServerPackage.VectoUtil;
import it.polimi.dist.ServerPackage.Server;

import java.util.ArrayList;

public class WriteMessage extends Message {

    protected ArrayList<Integer> ackNotReceived = new ArrayList<Integer>(); //todo in the write buffer


    public WriteMessage(int serverNumber) {
        super(serverNumber);
    }

    public void execute(Logic logic) {
        synchronized (logic.getServer()) {
            //otherwise:
            /*
            if(logic.writeBuffer.contains(this))
                return;
            */
            //this for avoid the reading of a write already present in the buffer
            /*
            case: alredy in the write buffer
            */
            for (int i = 0; i < logic.getWriteBuffer().size(); i++) {
                if (logic.getWriteBuffer().get(i).timestamp == this.timestamp
                        && logic.getWriteBuffer().get(i).serverNumber == this.serverNumber) {
                    reSendAck(logic);
                    return;
                }
            }
            /*
            case: write already done by this server
             */
            for (int i = 0; i < logic.getPerformedWrites().size(); i++) {
                if (logic.getPerformedWrites().get(i).timestamp == this.timestamp
                        && logic.getPerformedWrites().get(i).serverNumber == this.serverNumber) {
                    reSendAck(logic);
                    return;
                }
            }
            if (serverNumber != logic.getServerNumber()) {
                VectoUtil.addOne(logic, this.serverNumber);
            }
            //initialize the ackNotReceived setting all 1
            for (int i = 0; i < logic.getVectorClock().size(); i++) {
                ackNotReceived.add(1);
            }
            logic.getWriteBuffer().add(this);
            sendAck(logic);
        }
    }

    private void reSendAck(Logic logic) {
        for (int i = 0; i < logic.getTransmittedAcks().size(); i++) {
            if (logic.getTransmittedAcks().get(i).getWriteTimestamp() == this.timestamp
                    && logic.getTransmittedAcks().get(i).getWriteServerNumber() == this.serverNumber){
                System.out.println("RESEND AKC:");
                System.out.println(logic.getTransmittedAcks().get(i).toString());
                logic.getServer().sendMulti(logic.getTransmittedAcks().get(i));
                return;
            }
        }
    }

    @Override
    public void retransmission(Server server) {
        TimerThread timerThread = new TimerThread(this, server);
        String key = String.valueOf(this.getTimeStamp()).concat(String.valueOf(this.getServerNumber()));
        server.getLogic().getWriteRetransmissionTimers().put(key, timerThread);// add to HashMap in Logic with Message-Timer
        timerThread.start();
    }

    private void sendAck(Logic logic){
        AckMessage ack = new AckMessage(logic.getServerNumber());
        ack.fillReferences(this.timestamp,this.serverNumber);
        ack.setVectorClock(VectoUtil.addOne(logic, logic.getServerNumber()));
        logic.getTransmittedAcks().add(ack);
        logic.getServer().sendMulti(ack);
        System.out.println("ADDED TRASMITTED ACK  " + ack.getWriteTimestamp() + "  " + arrayToString(ack.getVectorClock()));
        System.out.println(ack.toString());
        if (logic.getServerNumber() != serverNumber){// the server which sent this message has already started the timer
            retransmission(logic.getServer());
        }
    }

    public ArrayList<Integer> getAckNotReceived() {
        return ackNotReceived;
    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nWRITE MESSAGE \nData ID: " + key + "\nValue: " + String.valueOf(data) + super.toString();
    }
}
