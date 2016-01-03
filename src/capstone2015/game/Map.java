package capstone2015.game;

import capstone2015.entity.*;

import static capstone2015.entity.EntityFactory.ID_EXIT;

import capstone2015.geom.Recti;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.*;

import static capstone2015.messaging.Message.Type.GameWon;

import capstone2015.util.Array2D;

import java.io.*;
import java.util.*;

public class Map implements MapInterface{
    private Array2D<Tile> tilemap;
    private LinkedList<Actor> actors;
    private Actor player; // for fast lookup;
    private MessageBus messageBus;

    private String mapName;

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

    private boolean isLegacySavegame(String fileName){
        FileInputStream file = null;
        try {
            file = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Properties props = new Properties();
        try {
            props.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Legacy properties always contain "Width"
        boolean is_legacy = props.containsKey("Width");

        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return is_legacy;
    }

    public void loadFromProperties(String fileName){
        if(isLegacySavegame(fileName)){
            loadFromLegacyProperties(fileName);
        } else {
            loadFromNewProperties(fileName);
        }
    }

    private void loadFromNewProperties(String fileName){
        Properties props = new Properties();
        try{
            props.load(new FileInputStream(new File(fileName)));
        } catch(Exception e){
            System.out.println("Failed to load map: " + e.getMessage());
        }

        int tiles_width = Integer.parseInt(props.getProperty("tiles.width"));
        int tiles_height = Integer.parseInt(props.getProperty("tiles.height"));

        if(props.containsKey("loaded_map_name")){
            mapName = props.getProperty("loaded_map_name");
        } else {
            mapName = fileName.substring(fileName.lastIndexOf("/"), fileName.lastIndexOf("."));
        }

        tilemap = new Array2D<Tile>(tiles_width, tiles_height);

        for(int i = 0; i < tiles_height; ++i){
            for(int j = 0; j < tiles_width; j++){
                int tile_id = Integer.parseInt(props.getProperty(String.format("tiles[%d,%d]", j, i)));
                tilemap.set(j, i, EntityFactory.createTile(tile_id));
            }
        }

        int entity_count = Integer.parseInt(props.getProperty("actors.count"));

        for(int i = 0; i < entity_count; ++i){
            int actor_id = Integer.parseInt(props.getProperty(String.format("actors[%d].id", i)));
            int actor_health = Integer.parseInt(props.getProperty(String.format("actors[%d].health", i)));
            int actor_xpos = Integer.parseInt(props.getProperty(String.format("actors[%d].pos.x", i)));
            int actor_ypos = Integer.parseInt(props.getProperty(String.format("actors[%d].pos.y", i)));
            int actor_inv_size = Integer.parseInt(props.getProperty(String.format("actors[%d].inventory.count", i)));

            TreeMap<String, Object> inst_params = new TreeMap<>();
            inst_params.put("Health", actor_health);

            if(actor_inv_size > 0){
                Inventory inv = new Inventory(actor_inv_size);
                for(int j = 0; j < actor_inv_size; ++j){
                    int actor_inv_item_id = Integer.parseInt(props.getProperty(String.format("actors[%d].inventory[%d]", i, j)));

                    if(actor_inv_item_id == -1)
                        inv.set(j, null);
                    else
                        inv.set(j, EntityFactory.createItem(actor_inv_item_id));
                }
                inst_params.put("Inventory", inv);

            }

            Actor actor = EntityFactory.createActor(actor_id, actor_xpos, actor_ypos, inst_params);

            if(actor_id == EntityFactory.ID_PLAYER)
                this.player = actor;

            add(actor);
        }
    }

    private void loadFromLegacyProperties(String fileName){
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

      tilemap = new Array2D<Tile>(width, height);

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
            case EntityFactory.ID_SWORD:
            case EntityFactory.ID_BOW:
            case EntityFactory.ID_ARROW:
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

      mapName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
    }

    public void storeToProperties(String fileName){
        FileOutputStream savefile = null;
        try {
            savefile = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();

        props.setProperty("tiles.width", Integer.toString(width()));
        props.setProperty("tiles.height", Integer.toString(height()));
        props.setProperty("loaded_map_name", mapName);
        props.setProperty("store_timestamp", Long.toString(System.currentTimeMillis()));

        for(int i = 0; i < height(); ++i){
            for(int j = 0; j < width(); ++j){
                props.setProperty(String.format("tiles[%d,%d]", j, i), Integer.toString(this.getTileAt(j, i).getProto().id));
            }
        }

        props.setProperty("actors.count", Integer.toString(actors.size()));

        for(int i = 0; i < actors.size(); ++i){
            Actor a = actors.get(i);

            props.setProperty(String.format("actors[%d].id", i), Integer.toString(a.getProto().id));
            props.setProperty(String.format("actors[%d].health", i), Integer.toString(a.getHealth()));
            props.setProperty(String.format("actors[%d].pos.x", i), Integer.toString(a.getPos().getX()));
            props.setProperty(String.format("actors[%d].pos.y", i), Integer.toString(a.getPos().getY()));
            if(a.getInventory() != null) {
                props.setProperty(String.format("actors[%d].inventory.count", i), Integer.toString(a.getInventory().size()));

                for(int j = 0; j < actors.get(i).getInventory().size(); ++j){
                    if(a.getInventory().get(j) != null)
                        props.setProperty(String.format("actors[%d].inventory[%d]", i, j), Integer.toString(a.getInventory().get(j).getProto().id));
                    else
                        props.setProperty(String.format("actors[%d].inventory[%d]", i, j), "-1");
                }
            }
            else
                props.setProperty(String.format("actors[%d].inventory.count", i), "0");
        }

        try {
            props.store(savefile, null);
        } catch (IOException e) {
            e.printStackTrace();
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

        if(     tilemap.inBounds(dest_pos.getX(), dest_pos.getY())
            &&  !getSolidTypeAt(dest_pos).collidesWith(entity.getSolidType())
        ){
            entity.setXPos(dest_pos.getX());
            entity.setYPos(dest_pos.getY());
            entity.onMoved(this.getMapEntitiesAt(dest_pos));
        } else {
            messageBus.enqueue(new Message(Message.Type.EntityMoveFailed, entity));
        }
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
  
    private void onInflictDamage(Actor damagingEntity, Vec2i position, int damage, int teamId){
        if(!this.inBounds(position.getX(), position.getY())){
            return;
        }
        
        ArrayList<Actor> entities = this.getActorsAt(position.getX(), position.getY());
        
        for(Actor e : entities){
            if(e.getTeamId() != teamId && !e.isInvulnerable()) {
                if(e.onDamage(damagingEntity, damage)) {
                    ReceivedDamageParams rdp = new ReceivedDamageParams();
                    rdp.damage = damage;
                    rdp.damagingEntity = damagingEntity;
                    rdp.damagedActor = e;
                    messageBus.enqueue(new Message(Message.Type.ReceivedDamage, rdp));
                }
            }
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
        Vec2i use_dir = akup.useDir;
        
        Tile tile = getTileAt(user.getPos().add(use_dir));
        
        if(tile.getProto().id == ID_EXIT){
            key.terminate();
            messageBus.enqueue(new Message(GameWon));
        }
    }

    private void onSpawnEffect(SpawnEffectParams sep){
        TreeMap<String, Object> instantiation_params = new TreeMap<>();
        instantiation_params.put("RepresentOverride", sep.represent);
        instantiation_params.put("Duration", sep.duration);
        Actor effect = EntityFactory.createActor(EntityFactory.ID_EFFECT, sep.pos, instantiation_params);
        this.add(effect);
    }

    private void onSpawnActor(SpawnActorParams sep){
        add(EntityFactory.createActor(sep.entityId, sep.position, sep.instantiationParams, sep.parent));
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
                    onMove(msg_obj.entity, msg_obj.direction);
                    break;
                }
                case InflictDamage:
                {
                    InflictDamageParams msg_obj = (InflictDamageParams)m.getMsgObject();
                    onInflictDamage(msg_obj.damagingEntity, msg_obj.position, msg_obj.damage, msg_obj.teamId);
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
                case SpawnEffect:
                {
                    onSpawnEffect((SpawnEffectParams)m.getMsgObject());
                    break;
                }
                case SpawnActor:
                {
                    onSpawnActor((SpawnActorParams)m.getMsgObject());
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

    @Override
    public LinkedList<Actor> getActors() {
        return actors;
    }

    public boolean inBounds(Vec2i pos) {
      return inBounds(pos.getX(), pos.getY());
    }
    
    public boolean inBounds(int x, int y){
      return tilemap.inBounds(x, y);
    }
}
