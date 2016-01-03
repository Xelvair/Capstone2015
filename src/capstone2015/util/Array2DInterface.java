package capstone2015.util;

import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;

public interface Array2DInterface<T> {
    default T get(Vec2i pos){
        return get(pos.getX(), pos.getY());
    }
    T get(int x, int y);
    boolean inBounds(Vec2i pos);
    boolean inBounds(int x, int y);
    void fill(T data);
    void set(Vec2i pos, T data);
    void set(int x, int y, T data);
    void insert(Array2DInterface<T> data, int x, int y);
    int width();
    int height();
    String toString();
    void insertCenterHorizontally(Array2D<T> array, int y);
    void insertCenterVertically(Array2D<T> array, int x);
    void insertCenter(Array2D<T> array);
    Array2DInterface<T> subArray(Recti subRect);
}
