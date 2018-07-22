/*package it.polimi.dist;

import it.polimi.dist.Messages.WriteMessage;

public class SendWrite implements Runnable{
    private Logic logic;
    private Server server;
    private WriteMessage message;

    public SendWrite(Logic logic, Server server, WriteMessage message){
        this.logic = logic;
        this.server = server;
        this.message=message;
    }

    public void run() {
        //TODO dopo che verifica che può deve scrivere il messaggio che ha in testa alla lista
        //this.server.getData().write(dataId,newData);
        message.setVectorClock(VectoClockUtil.addOne(logic));
        server.sendMulti(message);
        //logic.writeBuffer.removeFirst();

    }
}
*/