package it.polimi.dist.Model;

import it.polimi.dist.DataStorage;

public class AckJoinMessage extends Message{

    private int numberOfServers;
    private DataStorage dataStorage;

    public AckJoinMessage(int serverNumber, int numberOfServers, DataStorage dataStorage) {
        super(serverNumber);
        this.numberOfServers = numberOfServers;
        this.isNetMessage = true;
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServerNumber() == -1) {
            logic.setServerNumber(numberOfServers);
            logic.server.setStorage(dataStorage);
        }

    }
}
