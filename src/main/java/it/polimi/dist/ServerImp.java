package it.polimi.dist;

public class ServerImp implements Server {

    private DataStorage data;

    public ServerImp(){
        this.data= new DataStorage();
    };


    public int read(String dataId) {
        return 0;
    }

    public void write(String dataId, int newData) {

    }

    public static void main(){


    }

}
