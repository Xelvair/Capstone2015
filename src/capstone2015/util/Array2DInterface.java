package capstone2015.util;

import capstone2015.graphics.Panel;

public interface Array2DInterface<T> {
    public T get(int x, int y);
    public boolean inBounds(int x, int y);
    public void set(int x, int y, T data);
    public void insert(Array2DInterface<T> data, int x, int y);
    public int width();
    public int height();
    public String toString();
    public void insertCenterHorizontally(Array2D<T> array, int y);
    public void insertCenterVertically(Array2D<T> array, int x);
    public void insertCenter(Array2D<T> array);
}
