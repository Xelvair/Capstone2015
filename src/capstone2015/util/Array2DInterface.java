package capstone2015.util;

import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;

public interface Array2DInterface<T> {
    public default T get(Vec2i pos){
        return get(pos.getX(), pos.getY());
    }
    public T get(int x, int y);
    public boolean inBounds(Vec2i pos);
    public boolean inBounds(int x, int y);
    public void fill(T data);
    public void set(Vec2i pos, T data);
    public void set(int x, int y, T data);
    public void insert(Array2DInterface<T> data, int x, int y);
    public int width();
    public int height();
    public String toString();
    public void insertCenterHorizontally(Array2D<T> array, int y);
    public void insertCenterVertically(Array2D<T> array, int x);
    public void insertCenter(Array2D<T> array);
}
