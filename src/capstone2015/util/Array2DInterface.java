package capstone2015.util;

public interface Array2DInterface<T> {
    public T get(int x, int y);
    public boolean inBounds(int x, int y);
    public void set(int x, int y, T data);
    public void insert(int x, int y, Array2DInterface<T> data);
    public int width();
    public int height();
    public String toString();
}
