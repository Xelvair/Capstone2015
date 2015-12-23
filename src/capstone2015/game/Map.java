package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.entity.MapEntity;
import capstone2015.entity.Tile;
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
import java.util.Properties;

public class Map {
    private Array2D<Tile> tilemap;
    private Array2D<Boolean> revealmap;
    private LinkedList<Actor> actors;
    private Actor player; // for fast lookup;
    private MessageBus messageBus;

    public Map(MessageBus messageBus){
        this.messageBus = messageBus;
        actors = new LinkedList<>();
    }

    public void resetPlayer(int x, int y){
        removePlayer();
        player = EntityFactory.createActor(EntityFactory.ID_PLAYER, x, y);
        actors.add(player);
    }

    public void removePlayer(){
        if(player != null){
          actors.remove(player);
          player = null;
        }
  }
  
    public boolean isRevealed(int x, int y){
        return revealmap.get(x, y);
    }

    public void setRevealed(int x, int y){
        revealmap.set(x, y, true);
    }

    public Actor getPlayer(){
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
          tilemap.set(j, i, EntityFactory.createTile(EntityFactory.ID_FLOOR));
        }
      }
      
      revealmap.fill(false);

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
            case EntityFactory.ID_RATTLESNAKE:
            case EntityFactory.ID_KEY:
            case EntityFactory.ID_BONFIRE:
                actors.add(EntityFactory.createActor(tile_id, xcoord, ycoord));
                tilemap.set(xcoord, ycoord, EntityFactory.createTile(EntityFactory.ID_FLOOR));
                break;
            case EntityFactory.ID_ENTRY:
                resetPlayer(xcoord, ycoord);
                //intentional fallthrough
            default:
                tilemap.set(xcoord, ycoord, EntityFactory.createTile(tile_id));
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

    private void onMove(Actor entity, Direction dir){

        Vec2i cur_pos = new Vec2i(entity.getXPos(), entity.getYPos());
        Vec2i dest_pos = new Vec2i( cur_pos.getX() + dir.toVector().getX(), 
                                          cur_pos.getY() + dir.toVector().getY());

        if(!tilemap.inBounds(dest_pos.getX(), dest_pos.getY())){
            return;
        }

        if(!isSolidAt(dest_pos.getX(), dest_pos.getY())){
            entity.setXPos(dest_pos.getX());
            entity.setYPos(dest_pos.getY());
            entity.onMoved(this.getEntitiesAt(dest_pos));
        }
    }
  
    public boolean isSolidAt(int x, int y){
        boolean is_solid = false;

        ArrayList<MapEntity> local_entities = getEntitiesAt(x, y);
        for(MapEntity e : local_entities){
            if(e.isSolid()){
                is_solid = true;
            }
        }
        return is_solid;
    }
  
    public boolean isOpaqueAt(int x, int y){
        boolean is_opaque = false;

        ArrayList<MapEntity> local_entities = getEntitiesAt(x, y);
        for(MapEntity e : local_entities){
            if(e.isOpaque()){
                is_opaque = true;
            }
        }
        return is_opaque;
    }
  
    public void onInflictDamage(Actor damagingEntity, Vec2i position, int damage){
        if(!this.inBounds(position.getX(), position.getY())){
            return;
        }
        
        ArrayList<Actor> entities = this.getActorsAt(position.getX(), position.getY());
        
        for(Actor e : entities){
            e.onDamage(damagingEntity, damage);
        }
    }
    
    public void tick(double timeDelta){
       
        Iterator<Actor> it = actors.iterator();
        while(it.hasNext()){
            Actor e = it.next();
            
            e.onTick(timeDelta);
            
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

    public void add(Actor e){
        actors.add(e);
    }
  
    public ArrayList<MapEntity> getEntitiesAt(Vec2i pos){
        return getEntitiesAt(pos.getX(), pos.getY());
    }
    
    public ArrayList<MapEntity> getEntitiesAt(int x, int y){
      if(!tilemap.inBounds(x, y)){
        System.out.println("Map index out of bounds");
        throw new ArrayIndexOutOfBoundsException();
      }

      ArrayList<MapEntity> local_entities = new ArrayList<>();

      local_entities.add(tilemap.get(x, y));

      for(Actor e : actors){
          if(e.getXPos() == x && e.getYPos() == y){
              local_entities.add(e);
          }
      }

      return local_entities;
    }
    
    public ArrayList<Actor> getActorsAt(int x, int y){
        if(!tilemap.inBounds(x, y)){
          System.out.println("Map index out of bounds");
          throw new ArrayIndexOutOfBoundsException();
        }

        ArrayList<Actor> local_entities = new ArrayList<>();

        for(Actor e : actors){
            if(e.getXPos() == x && e.getYPos() == y){
                local_entities.add(e);
            }
        }

        return local_entities;
    }
  
    public boolean inBounds(Vec2i pos) {
      return inBounds(pos.getX(), pos.getY());
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
