package it.polimi.dist.Model;

import it.polimi.dist.DataStorage;

import java.util.Map;

public class AckJoinMessage extends Message{

    private int numberOfServers;
    private Map<String, Integer> dataStorage;

    public AckJoinMessage(int serverNumber, int numberOfServers, DataStorage dataStorage) {
        super(serverNumber);
        this.numberOfServers = numberOfServers;
        this.isNetMessage = true;
        this.dataStorage = dataStorage.getData();
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServerNumber() == -1) {
            logic.setServerNumber(numberOfServers);
            logic.inizializeVectorClock(numberOfServers);
            logic.server.setStorage(dataStorage);

        }

    }
}
