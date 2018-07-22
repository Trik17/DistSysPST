package it.polimi.dist.Model;

public class ClientReadMessage extends Message {
    private int result;


    public ClientReadMessage() {
        super(-1);
    }

    @Override
    public void execute(Logic logic) {
        result = logic.getServer().getData().read(key);
        logic.getServer().getClientHandler().

    }
}
