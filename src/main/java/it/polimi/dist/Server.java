package it.polimi.dist;

public interface Server {
    public int read(string dataId);
    public void write(string dataId, int newData);
}
