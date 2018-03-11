package it.polimi.dist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DataStorage {

    private Map<String, Integer> data; //data storage
    private ObjectMapper mapper;
    private FileReader fileReader;
    private File file;
    private FileWriter filewriter;

    public DataStorage(){
        this.mapper = new ObjectMapper();       //declare a new ObjectMapper variable
        this.mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            fileReader = new FileReader("src/main/resources/data.json");
        }catch (Exception e) {
            this.data = new HashMap(); // or HashMap<String, Integer>()
            file = new File("src/main/resources/data.json");
            try {
                file.createNewFile();
                filewriter = new FileWriter(file);
                writeToFile();
                //filewriter.close(); // ATTENZIONE
            } catch (IOException e1) {
               e.printStackTrace();
               System.out.println("qua 1");
            }
        }
    }

    private void writeToFile(){
        JSONObject json = new JSONObject();
        json.putAll( data );
        try {
            filewriter.write(json.toJSONString());
            filewriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("qua 2");
        }

    }

    public int read(String dataId) {

        return 0;
    }

    public void write(String dataId, int newData) {  //TODO

    }





    //TODO fare parte json
    //TODO quando viene avviato il costruttore devo controllare se c'è già un file json e a quel punto ricaricare quella hash e far partire il recovery
}
