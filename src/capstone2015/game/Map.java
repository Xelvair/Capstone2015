package capstone2015.game;

import capstone2015.geom.Recti;
import capstone2015.geom.Vector2i;
import capstone2015.graphics.Panel;
import capstone2015.util.Array2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

public class Map {
  private Array2D<Entity> tilemap;
  private LinkedList<PositionedEntity> entities;
  
  public void loadFromProperties(String fileName){
    Properties props = new Properties();
    try{
      props.load(new FileInputStream(new File(fileName)));
    } catch(Exception e){
      System.out.println("Failed to load map: " + e.getMessage());
    }
    
    int width = Integer.parseInt(props.getProperty("Width"));
    int height = Integer.parseInt(props.getProperty("Height"));
    
    /**
     * Remove width and height so we don't iterate over them
     * when filling the tile map
     */
    props.remove("Width");
    props.remove("Height");
    
    tilemap = new Array2D<>(width, height);
    
    for(int i = 0; i < height; i++){
      for(int j = 0; j < width; j++){
        tilemap.set(j, i, new Entity(Entity.ID_FLOOR));
      }
    }
    
    for(String key : props.stringPropertyNames()){
      String value = props.getProperty(key);
      String[] key_components = key.split(",");
      
      int xcoord = Integer.parseInt(key_components[0]);
      int ycoord = Integer.parseInt(key_components[1]);
      
      int tile_id = Integer.parseInt(value);
      
      //Special case for the enemy(dynamic obstacle)
      //the only dynamic thing that is stored on the map
      if(tile_id == Entity.ID_ENEMY){
        tilemap.set(xcoord, ycoord, new Entity(Entity.ID_FLOOR));
        //entities.add(SOMETHING??);
      } else {
        tilemap.set(xcoord, ycoord, new Entity(tile_id));
      }
    }
  }
  
  public int width(){
    return tilemap.width();
  }
  
  public int height(){
    return tilemap.height();
  }
  
  public ArrayList<Entity> getEntitiesAt(int x, int y){
    if(!tilemap.inBounds(x, y)){
      System.out.println("Map index out of bounds");
      throw new ArrayIndexOutOfBoundsException();
    }
    
    ArrayList<Entity> local_entities = new ArrayList<>();
    
    local_entities.add(tilemap.get(x, y));
    
    //TODO: add positioned entities to entity list
    
    return local_entities;
  }
  
  public boolean inBounds(int x, int y){
    return tilemap.inBounds(x, y);
  }
}
