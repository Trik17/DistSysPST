package it.polimi.dist.Model;

import it.polimi.dist.Server;

public class ClientWriteMessage extends Message{


    public ClientWriteMessage() {
        super(-1);
    }



    @Override
    public void execute(Logic logic) {
        logic.write(this.key, this.data);
    }




}
