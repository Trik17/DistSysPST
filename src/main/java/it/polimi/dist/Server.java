package it.polimi.dist;

public interface Server {
    public int read(String dataId);
    public void write(String dataId, int newData);
}
