package capstone2015.util;

import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;
import java.util.Arrays;

public class Array2D<T> implements Array2DInterface<T>{
    
    private Object[] data;
    private int width;
    private int height;
    
    public <T> Array2D(int width, int height){
        this.width = width;
        this.height = height;
        this.data = new Object[width * height];
    }
    
    @Override
    public T get(int x, int y) {
        if(!inBounds(x, y)){
            throw new ArrayIndexOutOfBoundsException(x + "," + y);
        }
        return (T)data[y * width + x];
    }

    @Override
    public void set(Vec2i pos, T data){
        set(pos.getX(), pos.getY(), data);
    }
    
    @Override
    public void set(int x, int y, T data) {
        if(!inBounds(x, y)){
            throw new ArrayIndexOutOfBoundsException(x + "," + y);
        }
        this.data[y * width + x] = data;
    }
    
    @Override
    public void fill(T data){
        Arrays.fill(this.data,  data);
    }

    @Override
    public void insert(Array2DInterface<T> data, int x, int y) {
        int startx = Math.max(0, -x);
        int starty = Math.max(0, -y);
        
        int endx = Math.min(data.width(), width - x);
        int endy = Math.min(data.height(), height - y);
        
        for(int i = starty; i < endy; i++){
            for(int j = startx; j < endx; j++){
                set(x + j, i + y, data.get(j, i));
            }
        }
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }
    
    @Override
    public String toString(){
        String str = "";
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                str += get(j, i);
            }
            str += "\n";
        }
        return str;
    }
    
    @Override
    public boolean inBounds(Vec2i pos) {
      return inBounds(pos.getX(), pos.getY());
    }
    
    @Override
    public boolean inBounds(int x, int y) {
      return (   x >= 0
              && x < width
              && y >= 0
              && y < height);
    }
  
    @Override
    public void insertCenterHorizontally(Array2D<T> array, int y){
        int x = (this.width() - array.width()) / 2;
        this.insert(array, x, y);
    }

    @Override
    public void insertCenterVertically(Array2D<T> array, int x){
        int y = (this.height() - array.height()) / 2;
        this.insert(array, x, y);
    }

    @Override
    public void insertCenter(Array2D<T> array){
        int y = (this.height() - array.height()) / 2;
        int x = (this.width() - array.width()) / 2;
        this.insert(array, x, y);
    }
    
}
