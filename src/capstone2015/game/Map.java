package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import static capstone2015.entity.EntityFactory.ID_EXIT;
import capstone2015.entity.Item;
import capstone2015.entity.MapEntity;
import capstone2015.entity.Tile;
import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.AttemptKeyUsageParams;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.GameWon;
import capstone2015.messaging.MessageBus;
import capstone2015.util.Array2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public class Map implements MapInterface{
    private Array2D<Tile> tilemap;
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
        add(player);
    }

    public void removePlayer(){
        if(player != null){
          actors.remove(player);
          player = null;
        }
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

      for(int i = 0; i < height; i++){
        for(int j = 0; j < width; j++){
          tilemap.set(j, i, EntityFactory.createTile(EntityFactory.ID_FLOOR));
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
            case EntityFactory.ID_RATTLESNAKE:
            case EntityFactory.ID_KEY:
            case EntityFactory.ID_BONFIRE:
            case EntityFactory.ID_HEALTH_POTION:
                add(EntityFactory.createActor(tile_id, xcoord, ycoord));
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
            entity.onMoved(this.getMapEntitiesAt(dest_pos));
        }
    }
  
    public boolean isSolidAt(int x, int y){
        boolean is_solid = false;

        ArrayList<MapEntity> local_entities = getMapEntitiesAt(x, y);
        for(MapEntity e : local_entities){
            if(e.isSolid()){
                is_solid = true;
            }
        }
        return is_solid;
    }
    
    public ArrayList<Actor> getPickupableAt(Vec2i pos){
        return getPickupableAt(pos.getX(), pos.getY());
    }
    
    public ArrayList<Actor> getPickupableAt(int x, int y){
        ArrayList<Actor> local_actors = getActorsAt(x, y);
        ArrayList<Actor> pickupable_actors = new ArrayList<>();
        
        for(Actor a : local_actors){
            if(a.isPickupable() && !a.isTerminated()){
                pickupable_actors.add(a);
            }
        }
        
        return pickupable_actors;
    }
  
    private void onInflictDamage(Actor damagingEntity, Vec2i position, int damage){
        if(!this.inBounds(position.getX(), position.getY())){
            return;
        }
        
        ArrayList<Actor> entities = this.getActorsAt(position.getX(), position.getY());
        
        for(Actor e : entities){
            e.onDamage(damagingEntity, damage);
        }
    }
    
    private void onPickup(Actor pickupper){
        int pickup_x = pickupper.getXPos();
        int pickup_y = pickupper.getYPos();

        ArrayList<Actor> picked_up_actors = this.getPickupableAt(pickup_x, pickup_y);
        
        while(!picked_up_actors.isEmpty()){
            Actor picked_up_actor = picked_up_actors.remove(0);
            
            Item picked_up_item = EntityFactory.createItemFromActor(picked_up_actor);
            
            if(pickupper.hasFreeInventorySlot()){
                if(pickupper.addItem(picked_up_item)){
                    picked_up_actor.terminate(); //remove from map

                    picked_up_item.onItemPickedUp(pickupper);
                    pickupper.onPickedUpItem(picked_up_item);
                }
            } else {
                pickupper.onPickedUpItemFailedNoSpace(picked_up_item);
            }
        }
    }
    
    private void onDrop(Actor dropper){
        Inventory dropper_inv = dropper.getInventory();
        Item dropped_item = dropper_inv.getSelectedItem();
        if(dropped_item != null){
            Actor dropped_actor = EntityFactory.createActorFromItem(
                    dropped_item, 
                    dropper.getPos()
            );
            if(dropped_actor != null){
                actors.add(dropped_actor);
                dropper_inv.remove(dropped_item);
                
                dropper.onDroppedItem(dropped_item);
                dropped_item.onItemDropped();
                
            }
        }
    }
    
    private void onAttemptKeyUsage(AttemptKeyUsageParams akup){
        Actor user = akup.user;
        Item key = akup.key;
        
        Tile tile = getTileAt(user.getPos());
        
        if(tile.getProto().id == ID_EXIT){
            key.terminate();
            messageBus.enqueue(new Message(GameWon));
        }
    }
    
    public void tick(double timeDelta){
       
        Iterator<Actor> it = actors.iterator();
        while(it.hasNext()){
            Actor e = it.next();
            
            if(e.hasVision()){
                int vis_radius = e.getVisionRadius();
                Vec2i pos = e.getPos();
                Recti vision_rect = new Recti(
                        pos.getX() - vis_radius,
                        pos.getY() - vis_radius,
                        vis_radius * 2,
                        vis_radius * 2
                );
                Array2D<Boolean> vision_mask = VisionMaskGenerator.generate(this, vision_rect, e);
                e.onVisionUpdate(vision_rect.getLeft(), vision_rect.getTop(), vision_mask);
            }
            
            e.onTick(timeDelta);
            
            if(e.isTerminated()){
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
                case Pickup:
                {
                    onPickup((Actor)m.getMsgObject());
                    break;
                }
                case Drop:
                {
                    onDrop((Actor)m.getMsgObject());
                    break;
                }
                case AttemptKeyUsage:
                {
                    onAttemptKeyUsage((AttemptKeyUsageParams)m.getMsgObject());
                    break;
                }
            }
        }
    }

    public void add(Actor e){
        e.setMap(this);
        actors.add(e);
    }
    
    public Tile getTileAt(Vec2i pos){
        return getTileAt(pos.getX(), pos.getY());
    }
    
    public Tile getTileAt(int x, int y){
        if(!tilemap.inBounds(x, y)){
            System.out.println("Map index out of bounds");
            throw new ArrayIndexOutOfBoundsException();
        }
        return tilemap.get(x, y);
    }
    
    public ArrayList<MapEntity> getMapEntitiesAt(int x, int y){
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
}
