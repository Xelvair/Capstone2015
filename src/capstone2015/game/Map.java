package capstone2015.game;

import capstone2015.util.Array2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

public class Map {
  private Array2D<Entity> tilemap;
  private LinkedList<PositionedEntity> entities;
  private PositionedEntity player; // for fast lookup;
  
  public Map(){
      entities = new LinkedList<>();
  }
  
  public void resetPlayer(int x, int y){
      removePlayer();
      player = new PositionedEntity(Entity.ID_PLAYER, x, y);
      entities.add(player);
  }
  
  public void removePlayer(){
      if(player != null){
        entities.remove(player);
        player = null;
      }
  }
  
  public PositionedEntity getPlayer(){
      return player;
  }
  
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
      
      /**
       * Special case for the enemy, key and static obstacle,
       * as they will instead be added to the list of movable entities
       * rather than static tiles.
       */
      switch(tile_id){
          case Entity.ID_ENEMY:
          case Entity.ID_KEY:
          case Entity.ID_STATIC_OBSTACLE:
              entities.add(new PositionedEntity(tile_id, xcoord, ycoord));
              tilemap.set(xcoord, ycoord, new Entity(Entity.ID_FLOOR));
              break;
          default:
              tilemap.set(xcoord, ycoord, new Entity(tile_id));
              break;
      }
    }
  }
  
  public int width(){
    return tilemap.width();
  }
  
  public int height(){
    return tilemap.height();
  }
  
  public void tick(double timeDelta){
      for(PositionedEntity entity : entities){
          EntityAction action = entity.tick(timeDelta);
          switch(action.getType()){
              case MOVE_LEFT:
                  entity.setXPos(entity.getXPos() - 1);
                  break;
              case MOVE_RIGHT:
                  entity.setXPos(entity.getXPos() + 1);
                  break;
              case MOVE_UP:
                  entity.setYPos(entity.getYPos() - 1);
                  break;
              case MOVE_DOWN:
                  entity.setYPos(entity.getYPos() + 1);
                  break;
              case NONE:
                  break;
              case TERMINATE:
                  break;
          }
      }
  }
  
  public void add(PositionedEntity e){
      entities.add(e);
  }
  
  public ArrayList<Entity> getEntitiesAt(int x, int y){
    if(!tilemap.inBounds(x, y)){
      System.out.println("Map index out of bounds");
      throw new ArrayIndexOutOfBoundsException();
    }
    
    ArrayList<Entity> local_entities = new ArrayList<>();
    
    local_entities.add(tilemap.get(x, y));
    
    for(PositionedEntity e : entities){
        if(e.getXPos() == x && e.getYPos() == y){
            local_entities.add(e);
        }
    }
    
    return local_entities;
  }
  
  public boolean inBounds(int x, int y){
    return tilemap.inBounds(x, y);
  }
}
