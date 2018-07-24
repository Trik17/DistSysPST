package it.polimi.dist.Messages;

import it.polimi.dist.DataStorage;
import it.polimi.dist.Model.Logic;

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

    public int getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(int numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    public Map<String, Integer> getDataStorage() {
        return dataStorage;
    }

    public void setDataStorage(Map<String, Integer> dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public void execute(Logic logic) {
        if(logic.getServerNumber() == -1) {
            logic.setServerNumber(numberOfServers);
            logic.inizializeVectorClock(numberOfServers);
            logic.getServer().setStorage(dataStorage);

        }

    }

    @Override
    public String toString() {
        return "<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \nACK JOIN \nNumber of Servers: " + String.valueOf(numberOfServers) + super.toString();
    }
}
