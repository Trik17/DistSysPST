package it.polimi.dist.ServerPackage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataStorage implements Serializable{

    private Map<String, Integer> data; //data storage
    private ObjectMapper mapper;
    private FileReader fileReader;
    private File file;
    private FileWriter filewriter;

    public DataStorage(){
        this.mapper = new ObjectMapper();       //declare a new ObjectMapper variable
        this.mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.data = new HashMap(); // or HashMap<String, Integer>()
        ///*TODO togliere da qua per  avere json
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
            //e.printStackTrace();
            System.out.println("no map in .json file: new server, it's not a reboot");

        }//todo fino a qua (più il commento nella funzione write */
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

    public Map<String, Integer> getData() {
        return data;
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        writeToFile();
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

    public String read(String dataId) {
        return String.valueOf(this.data.get(dataId));

    }

    public void write(String dataId, int newData) {  //TODO
        this.data.put(dataId,newData);
        writeToFile(); //todo togliendo il commento(QUA E NEL COSTRUTTORE) si fa il file json
    }
    /*public static void main(String[] args) {
        ArrayList<Long> index = new ArrayList<Long>();
        ArrayList<Long> index2 = new ArrayList<Long>();
        long a=3;
        long b = 644674847;
        long c = 3;
        long d = 644674847;
        index.add(a);
        index.add(b);
        index2.add(c);
        index2.add(d);
        if (index.equals(index2))
            System.out.println(" funziona equals");
        if (index==index2)
            System.out.println(" funziona ==");
        if (!index.equals(index2))
            System.out.println("NON funziona equals");
    /*
        while(true) {
            DataStorage dataStorage = new DataStorage();
            System.out.println("Which action do you want to execute? \n (R) Read - (W) Write");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.next();
            String key;
            int data;
            if ("W".equals(choice)) {
                System.out.println("Insert the data ID you want to modify");
                scanner = new Scanner(System.in);
                key = scanner.next();
                System.out.println("Insert the value you want to write");
                data = scanner.nextInt();
                dataStorage.write(key, data);
            } else if ("R".equals(choice)) {
                System.out.println("Insert the data ID you want to read");
                scanner = new Scanner(System.in);
                key = scanner.next();
                System.out.println(dataStorage.read(key));
            } else {
                System.out.println("Invalid Input, ");
            }
        }
    }*/
    //QUESTO RISOLTO CON ALTRI METODI
    //TODO quando viene avviato il costruttore devo controllare se c'è già un file json e a quel punto ricaricare quella hash e far partire il recovery
}
