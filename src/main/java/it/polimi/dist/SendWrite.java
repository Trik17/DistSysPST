package it.polimi.dist;

public class SendWrite implements Runnable{
    private Logic logic;
    private Server server;

    public SendWrite(Logic logic, Server server){
        this.logic = logic;
        this.server = server;
    }

    public void run() {
        //TODO dopo che verifica che può deve scrivere il messaggio che ha in testa alla lista
        //this.server.getData().write(dataId,newData);
        synchronized (logic) {
            while(!logic.getResendBuffer().isEmpty()){
                /*try {
                    wait(1); //TODO ok? o un while vuoto o con una wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            server.sendMulti(logic.getWriteBuffer().getFirst());


            logic.getWriteBuffer().removeFirst();
        }
    }
}
