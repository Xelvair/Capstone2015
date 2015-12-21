package capstone2015.game;

import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.util.Array2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Properties;

public class Map {
    private Array2D<Entity> tilemap;
    private Array2D<Boolean> revealmap;
    private LinkedList<ActiveEntity> entities;
    private ActiveEntity player; // for fast lookup;
    private MessageBus messageBus;

    public Map(MessageBus messageBus){
        this.messageBus = messageBus;
        entities = new LinkedList<>();
    }

    public void resetPlayer(int x, int y){
        removePlayer();
        player = new ActiveEntity(Entity.ID_PLAYER, x, y, messageBus);
        entities.add(player);
    }

    public void removePlayer(){
        if(player != null){
          entities.remove(player);
          player = null;
        }
  }
  
    public boolean isRevealed(int x, int y){
        return revealmap.get(x, y);
    }

    public void setRevealed(int x, int y){
        revealmap.set(x, y, true);
    }

    public ActiveEntity getPlayer(){
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
      revealmap = new Array2D<>(width, height);

      for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
          tilemap.set(j, i, new Entity(Entity.ID_FLOOR, null));
        }
      }
      
      for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
          revealmap.set(j, i, false);
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
                entities.add(new ActiveEntity(tile_id, xcoord, ycoord, messageBus));
                tilemap.set(xcoord, ycoord, new Entity(Entity.ID_FLOOR, null));
                break;
            default:
                tilemap.set(xcoord, ycoord, new Entity(tile_id, null));
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

    private void onMove(ActiveEntity entity, Direction dir){

        Vec2i cur_pos = new Vec2i(entity.getXPos(), entity.getYPos());
        Vec2i dest_pos = new Vec2i( cur_pos.getX() + dir.toVector().getX(), 
                                          cur_pos.getY() + dir.toVector().getY());

        if(!tilemap.inBounds(dest_pos.getX(), dest_pos.getY())){
            return;
        }

        if(!isSolidAt(dest_pos.getX(), dest_pos.getY())){
            entity.setXPos(dest_pos.getX());
            entity.setYPos(dest_pos.getY());
            OnMovedBehavior omb = entity.getOnMovedBehavior();
            if(omb != null){
                omb.invoke(entity, this.getEntitiesAt(dest_pos), messageBus);
            }
        }
    }
  
    public boolean isSolidAt(int x, int y){
        boolean is_solid = false;

        ArrayList<Entity> local_entities = getEntitiesAt(x, y);
        for(Entity e : local_entities){
            if(e.isSolid()){
                is_solid = true;
            }
        }
        return is_solid;
    }
  
    public boolean isOpaqueAt(int x, int y){
        boolean is_opaque = false;

        ArrayList<Entity> local_entities = getEntitiesAt(x, y);
        for(Entity e : local_entities){
            if(e.isOpaque()){
                is_opaque = true;
            }
        }
        return is_opaque;
    }
  
    public void onInflictDamage(ActiveEntity damagingEntity, Vec2i position, int damage){
        if(!this.inBounds(position.getX(), position.getY())){
            return;
        }
        
        ArrayList<ActiveEntity> entities = this.getActiveEntitiesAt(position.getX(), position.getY());
        
        for(ActiveEntity e : entities){
            if(e.getOnDamageBehavior() != null){
                e.getOnDamageBehavior().invoke(e, damagingEntity, damage, messageBus);
            }
        }
    }
    
    public void tick(double timeDelta){
       
        Iterator<ActiveEntity> it = entities.iterator();
        while(it.hasNext()){
            ActiveEntity e = it.next();
            
            if(e.getOnTickBehavior() != null){
                e.getOnTickBehavior().invoke(e, timeDelta, messageBus);
            }
            
            if(e.isTerminate()){
                it.remove();
            }
        }
        
        
        for(Message m : messageBus){
            switch(m.getType()){
                case EntityMove:
                {
                    EntityMoveParams msg_obj = (EntityMoveParams)m.getMsgObject();
                    onMove(msg_obj.getEntity(), msg_obj.getDirection());
                    break;
                }
                case InflictDamage:
                {
                    InflictDamageParams msg_obj = (InflictDamageParams)m.getMsgObject();
                    onInflictDamage(msg_obj.getDamagingEntity(), msg_obj.getPosition(), msg_obj.getDamage());
                    break;
                }
            }
        }
    }

    public void add(ActiveEntity e){
        entities.add(e);
    }
  
    public ArrayList<Entity> getEntitiesAt(Vec2i pos){
        return getEntitiesAt(pos.getX(), pos.getY());
    }
    
    public ArrayList<Entity> getEntitiesAt(int x, int y){
      if(!tilemap.inBounds(x, y)){
        System.out.println("Map index out of bounds");
        throw new ArrayIndexOutOfBoundsException();
      }

      ArrayList<Entity> local_entities = new ArrayList<>();

      local_entities.add(tilemap.get(x, y));

      for(ActiveEntity e : entities){
          if(e.getXPos() == x && e.getYPos() == y){
              local_entities.add(e);
          }
      }

      return local_entities;
    }
    
    public ArrayList<ActiveEntity> getActiveEntitiesAt(int x, int y){
        if(!tilemap.inBounds(x, y)){
          System.out.println("Map index out of bounds");
          throw new ArrayIndexOutOfBoundsException();
        }

        ArrayList<ActiveEntity> local_entities = new ArrayList<>();

        for(ActiveEntity e : entities){
            if(e.getXPos() == x && e.getYPos() == y){
                local_entities.add(e);
            }
        }

        return local_entities;
    }
  
    public boolean inBounds(int x, int y){
      return tilemap.inBounds(x, y);
    }

    void applyVisionMask(Array2D<Boolean> vision_mask, int x, int y) {       
        for(int i = 0; i < vision_mask.height(); i++){
            for(int j = 0; j < vision_mask.width(); j++){
                int map_x = x + j;
                int map_y = y + i;
                
                if(inBounds(map_x, map_y) && vision_mask.get(j, i)){
                    setRevealed(map_x, map_y);
                }
            }
        }
    }
}
