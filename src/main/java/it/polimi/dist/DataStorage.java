package it.polimi.dist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        this.data = new HashMap(); // or HashMap<String, Integer>()
        try {
            fileReader = new FileReader("src/main/resources/data.json");
            //filewriter = new FileWriter("src/main/resources/data.json");
            TypeReference<HashMap<String, Integer>> mapTypeJ= new TypeReference<HashMap<String, Integer>>() {};
            try{
                this.data= mapper.readValue(fileReader,mapTypeJ);
                fileReader.close();
                System.out.println("found map in .json file: it's a reboot");
            } catch (JsonMappingException e1) {
                System.out.println("no map in .json file: new server, it's not a reboot");
                //TODO OGNI VOLTA BISOGNA RESETTARE IL FILE
                //TODO: questo e il caso di non reboot
                //e1.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
           /*
            file = new File("src/main/resources/data.json");

            /*try {
                file.createNewFile();
                filewriter = new FileWriter(file);
                writeToFile();
                //filewriter.close(); // ATTENZIONE
            } catch (IOException e1) {
               e.printStackTrace();
               //System.out.println("errore 1");
            }*/
        }
        //DA QUA PER PROVA FILE:
        //TODO CANCELLA:
        /*int i= data.get("miriam");
        System.out.println("AAAAAAAAAA:");
        System.out.printf("%d",i); */
       /* write("andrea", 17);
        write("miriam", 3);
        write("santa", 4);
        */
    }



    private void writeToFile(){
        JSONObject json = new JSONObject();
        json.putAll( data );
        try {
            filewriter = new FileWriter("src/main/resources/data.json");
            String mapString=json.toJSONString();
            filewriter.write(mapString);
            filewriter.flush();
            filewriter.close(); // ATTENZIONE
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("qua 2");
        }

    }

    public int read(String dataId) {
        try {
            return this.data.get(dataId);
        } catch (NullPointerException n){
            return Integer.parseInt(null);
        }
    }

    public void write(String dataId, int newData) {  //TODO
        this.data.put(dataId,newData);
        writeToFile();
    }





    //TODO fare parte json
    //TODO quando viene avviato il costruttore devo controllare se c'è già un file json e a quel punto ricaricare quella hash e far partire il recovery
}
