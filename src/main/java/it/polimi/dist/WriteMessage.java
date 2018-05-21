package it.polimi.dist;

public class WriteMessage extends Message {

    public WriteMessage() {
        super();
    }

    @Override
    public void execute(Server server) {
        server.addMsgQueue(this);

    }

    @Override
    public void fill() {
        super.fill();
    }
}
