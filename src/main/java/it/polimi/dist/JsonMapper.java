/*package it.polimi.dist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileReader;
import java.io.IOException;


public class JsonMapper {

    private FileReader file;

    public JsonMapper() {


    }


        try{
            fileT= new FileReader("src/main/resources/cards/territory.json");
            fileC= new FileReader("src/main/resources/cards/character.json");
            fileB= new FileReader("src/main/resources/cards/building.json");
            fileV= new FileReader("src/main/resources/cards/venture.json");
            fileAS= new FileReader("src/main/resources/actionSpace.json");
            fileET= new FileReader("src/main/resources/excommunicationTiles.json");
            fileLC= new FileReader("src/main/resources/cards/leaderCards.json");
            filePBT= new FileReader("src/main/resources/personalBonusTile.json");

            //PersonalBonusTiles
            TypeReference<List<PersonalBonusTile>> mapTypePBT = new TypeReference<List<PersonalBonusTile>>() {};
            personalBonusTiles=mapper.readValue(filePBT,mapTypePBT);

            //LeaderCards
            TypeReference<List<LeaderCard>> mapTypeL = new TypeReference<List<LeaderCard>>() {};
            leaderCards=mapper.readValue(fileLC,mapTypeL);

            //Cards
            TypeReference<List<TerritoryCard>> mapTypeT = new TypeReference<List<TerritoryCard>>() {};
            territoryCards=mapper.readValue(fileT,mapTypeT);

            TypeReference<List<CharacterCard>> mapTypeC = new TypeReference<List<CharacterCard>>() {};
            characterCards=mapper.readValue(fileC,mapTypeC);

            TypeReference<List<BuildingCard>> mapTypeB = new TypeReference<List<BuildingCard>>() {};
            buildingCards=mapper.readValue(fileB,mapTypeB);

            TypeReference<List<VentureCard>> mapTypeV = new TypeReference<List<VentureCard>>() {};
            ventureCards=mapper.readValue(fileV,mapTypeV);

            //ActionSpaces
            TypeReference<List<ActionSpace>> mapTypeAS = new TypeReference<List<ActionSpace>>() {};
            actionSpaces=mapper.readValue(fileAS,mapTypeAS);

            //excommunicationTiles
            TypeReference<List<ExcommunicationTile>> mapTypeET = new TypeReference<List<ExcommunicationTile>>() {};
            excommunicationTiles=mapper.readValue(fileET,mapTypeET);

        } catch (IOException e) {
            JsonMapper.errorJson(e);
        }

    }

    public TerritoryCard[] getTerritoryCardArray(){
        return territoryCards.toArray(new TerritoryCard[0]);
    }

    public CharacterCard[] getCharacterCardArray(){
        return characterCards.toArray(new CharacterCard[0]);
    }

    public BuildingCard[] getBuildingCardArray(){
        return buildingCards.toArray(new BuildingCard[0]);
    }

    public LeaderCard[] getLeaderCards() {
        return leaderCards.toArray(new LeaderCard[0]);
    }

    public VentureCard[] getVentureCardsArray(){
        return ventureCards.toArray(new VentureCard[0]);
    }

    public PersonalBonusTile[] getPersonalBonusTiles() {
        return personalBonusTiles.toArray(new PersonalBonusTile[0]);
    }

    public List<ActionSpace> getActionSpaces(){
        return actionSpaces;
    }

    public List<ExcommunicationTile> getExcommunicationTile(){
        return excommunicationTiles;
    }

    public static void timerFromJson()  {
        try{
            ObjectMapper mapper = new ObjectMapper();       //declare a new ObjectMapper variable
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            FileReader fileTimer= new FileReader("src/main/resources/timer.json");
            /*TimerJson t=mapper.readValue(fileTimer, TimerJson.class);
        }catch (IOException e) {
            JsonMapper.errorJson(e);
        }
    }

    public static void errorJson(IOException e){
        System.out.println("error .json files");
        e.printStackTrace();
    }

}
*/

