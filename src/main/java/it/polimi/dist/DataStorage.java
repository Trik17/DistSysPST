package it.polimi.dist;

import java.util.HashMap;
import java.util.Map;

public class DataStorage {

    private Map<String, Integer> data;

    public DataStorage(){
        this.data = new HashMap();

    }

    //TODO fare parte json
    //TODO quando viene avviato il costruttore devo controllare se c'è già un file json e a quel punto ricaricare quella hash e far partire il recovery
}
