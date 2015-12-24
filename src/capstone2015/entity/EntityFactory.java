package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.Map;
import capstone2015.game.behavior.DamageOnCollisionOnTickBehavior;
import capstone2015.game.behavior.HealthPotionOnUseBehavior;
import capstone2015.game.behavior.KeyOnUseBehavior;
import capstone2015.game.behavior.PlayerOnDamageBehavior;
import capstone2015.game.behavior.PlayerOnDroppedItemBehavior;
import capstone2015.game.behavior.PlayerOnHealBehavior;
import capstone2015.game.behavior.PlayerOnMovedBehavior;
import capstone2015.game.behavior.PlayerOnPickedUpItemBehavior;
import capstone2015.game.behavior.PlayerOnTickBehavior;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.MessageBus;
import java.awt.Color;
import java.util.ArrayList;

/***********************************
 * Creates Tiles, Items and Actors from a shared pool of entity
 * prototypes. Note that there are no subclasses to Actor, Item or Tile.
 * This is intentional. A good design principle in game (and software in 
 * general) development is to favor composition over inheritance!
 * https://en.wikipedia.org/wiki/Composition_over_inheritance
 */
public class EntityFactory {
    public static final int ID_WALL = 0;
    public static final int ID_ENTRY = 1;
    public static final int ID_EXIT = 2;
    public static final int ID_BONFIRE = 3;
    public static final int ID_RATTLESNAKE = 4;
    public static final int ID_KEY = 5;
    public static final int ID_FLOOR = 6;
    public static final int ID_PLAYER = 7;
    public static final int ID_HEALTH_POTION = 8;
    
    public static final Color COLOR_FLOOR = new Color(87,59,12);
    public static final Color COLOR_FLOOR_HIDDEN = new Color(26, 20, 4);
    
    private static ArrayList<EntityProto> entityProtos;
    private static MessageBus messageBus;
    
    public static EntityProto getProto(int entityProtoId){
        if(entityProtos == null){
            loadEntityProtos();
        }
        return entityProtos.get(entityProtoId);
    }
    
    public static void setMessageBus(MessageBus messageBus){
        EntityFactory.messageBus = messageBus;
    }
    
    public static Tile createTile(int entityProtoId){
        try{
            EntityProto e_proto = getProto(entityProtoId);

            if(   
                  e_proto.entityBaseProto == null
                || e_proto.mapEntityProto == null
                || e_proto.tileProto == null
            ){
                System.out.println("Failed to create tile with id #" + entityProtoId);
                return null;
            }

            Tile tile = new Tile();
            tile.messageBus = messageBus;
            tile.proto = e_proto;
            if(e_proto.mapEntityProto.onWalkedOverBehaviorClass != null){
                tile.onWalkedOverBehavior = (e_proto.mapEntityProto.onWalkedOverBehaviorClass.newInstance());
            }
        
            return tile;
        } catch(Exception e){
            System.out.println("Failed to create Tile with id #" + entityProtoId);
            return null;
        }
    }
    
    public static Actor createActor(int entityProtoId){
        return createActor(entityProtoId, 0, 0);
    }
    public static Actor createActor(int entityProtoId, Vec2i pos){
        return createActor(entityProtoId, pos.getX(), pos.getY());
    }
    public static Actor createActor(int entityProtoId, int x, int y){
        try{
            EntityProto e_proto = getProto(entityProtoId);

            if(   
                  e_proto.entityBaseProto == null
                || e_proto.mapEntityProto == null
                || e_proto.actorProto == null
            ){
                System.out.println("Failed to create Actor with id #" + entityProtoId);
                return null;
            }

            Actor actor = new Actor();
            actor.proto = e_proto;
            actor.messageBus = messageBus;
            actor.pos = new Vec2i(x, y);
            actor.health = e_proto.actorProto.maxHealth;
            actor.visionRadius = e_proto.actorProto.visionRadius;
            
            if(e_proto.actorProto.onMovedBehaviorClass != null){
                actor.onMovedBehavior = e_proto.actorProto.onMovedBehaviorClass.newInstance();
            }
            if(e_proto.actorProto.onTickBehaviorClass != null){
                actor.onTickBehavior = e_proto.actorProto.onTickBehaviorClass.newInstance();
            }
            if(e_proto.mapEntityProto.onWalkedOverBehaviorClass != null){
                actor.onWalkedOverBehavior = e_proto.mapEntityProto.onWalkedOverBehaviorClass.newInstance();
            }
            if(e_proto.actorProto.onDamageBehaviorClass != null){
                actor.onDamageBehavior = e_proto.actorProto.onDamageBehaviorClass.newInstance();
            }
            if(e_proto.actorProto.onPickedUpItemBehaviorClass != null){
                actor.onPickedUpItemBehavior = e_proto.actorProto.onPickedUpItemBehaviorClass.newInstance();
            }
            if(e_proto.actorProto.onDroppedItemBehaviorClass != null){
                actor.onDroppedItemBehavior = e_proto.actorProto.onDroppedItemBehaviorClass.newInstance();
            }
            if(e_proto.actorProto.onHealBehaviorClass != null){
                actor.onHealBehavior = e_proto.actorProto.onHealBehaviorClass.newInstance();
            }
            
            if(e_proto.actorProto.inventorySize > 0){
                actor.inventory = new Inventory(e_proto.actorProto.inventorySize);
            }
        
            return actor;
        } catch(Exception e){
            System.out.println("Failed to create Actor with id #" + entityProtoId);
            return null;
        }
    }
    
    public static Item createItem(int entityProtoId){
        try{
            EntityProto e_proto = getProto(entityProtoId);

            if(   
                  e_proto.entityBaseProto == null
                || e_proto.itemProto == null
            ){
                System.out.println("Failed to create Item with id #" + entityProtoId);
                return null;
            }
            
            Item item = new Item();
            item.proto = e_proto;
            item.messageBus = messageBus;
            if(e_proto.itemProto.onItemPickedUpBehaviorClass != null){
                item.onItemPickedUpBehavior = e_proto.itemProto.onItemPickedUpBehaviorClass.newInstance();
            }
            if(e_proto.itemProto.onItemDroppedBehaviorClass != null){
                item.onItemDroppedBehavior = e_proto.itemProto.onItemDroppedBehaviorClass.newInstance();
            }
            if(e_proto.itemProto.onUseBehaviorClass != null){
                item.onUseBehavior = e_proto.itemProto.onUseBehaviorClass.newInstance();
            }

            return item;
        } catch(Exception e){
            System.out.println("Failed to create Item with id #" + entityProtoId);
            return null;
        }
    }
    
    public static Actor createActorFromItem(Item item, Vec2i pos){
        return createActorFromItem(item, pos.getX(), pos.getY());
    }
    public static Actor createActorFromItem(Item item, int x, int y){
        return createActor(item.proto.id, x, y);
    }
    
    public static Item createItemFromActor(Actor actor){
        return createItem(actor.proto.id);
    }
    
    /** 
     * I was going to load that stuff from a JSON,
     * then I noticed I wasn't allowed to - meh.
     */
    private static void loadEntityProtos(){     
        entityProtos = new ArrayList<>();
        EntityProto ep;
        /******************************************
         * #0 - WALL - TILE
         */
        ep = new EntityProto(ID_WALL);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar(' ', Color.WHITE, new Color(139, 141, 122));
        ep.entityBaseProto.name = "Wall";
        ep.entityBaseProto.description = 
              "Walls make up most of the dungeon. You can not\n"
            + "walk over them, you can not see past them, and they\n"
            + "stop you from running away from your foes.\n"
            + "Be very wary of these strange creatures.";
        ep.mapEntityProto.isOpaque = true;
        ep.mapEntityProto.isSolid = true;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, new Color(46,47,45));
        
        entityProtos.add(ep);
        
        /******************************************
         * #1 - ENTRY - TILE
         */
        ep = new EntityProto(ID_ENTRY);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\u25BC', Color.BLUE, COLOR_FLOOR);
        ep.entityBaseProto.name = "Entry";
        ep.entityBaseProto.description = 
              "The place where you entered the dungeon.\n"
            + "There's no turning back now!";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u25BC', Color.BLUE, COLOR_FLOOR_HIDDEN);
        
        entityProtos.add(ep);
        
        /******************************************
         * #2 - EXIT - TILE
         */
        ep = new EntityProto(ID_EXIT);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\u25B2', Color.GREEN, COLOR_FLOOR);
        ep.entityBaseProto.name = "Exit";
        ep.entityBaseProto.description = 
              "You need to find this exit in order to leave the\n"
            + "dungeon. However, you cannot leave until you have\n"
            + "found a dungeon key.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u25B2', Color.GREEN, COLOR_FLOOR_HIDDEN);
        
        entityProtos.add(ep);
        
        /******************************************
         * #3 - BONFIRE - ACTOR
         */
        ep = new EntityProto(ID_BONFIRE);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\uFB63', new Color(156, 42, 0), COLOR_FLOOR);
        ep.entityBaseProto.name = "Bonfire";
        ep.entityBaseProto.description = 
              "A warm and cozy bonfire.\n"
            + "Don't even think about stepping on it, it will burn you!";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\uFB63', new Color(156, 42, 0), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = DamageOnCollisionOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;

        entityProtos.add(ep);
        
        /******************************************
         * #4 - RATTLESNAKE - ACTOR
         */
        ep = new EntityProto(ID_RATTLESNAKE);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\u08B0', new Color(0, 153, 76), COLOR_FLOOR);
        ep.entityBaseProto.name = "Rattlesnake";
        ep.entityBaseProto.description = 
              "This dangerous creature can become very deadly \n"
            + "very quick if you come too close to it. Keep your\n"
            + "distance, or be prepared for a fight.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u08B0', new Color(0, 153, 76), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = 1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = DamageOnCollisionOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 7;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;

        entityProtos.add(ep);
        
        /******************************************
         * #5 - KEY - ACTOR, ITEM
         */
        ep = new EntityProto(ID_KEY);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\u2C61', Color.YELLOW, COLOR_FLOOR);
        ep.entityBaseProto.name = "Dungeon Key";
        ep.entityBaseProto.description = 
              "This is the key you need to find in order to leave\n"
            + "this dungeon. Once you have found this key, it will show\n"
            + "up in your inventory, and you will be able to leave\n"
            + "through any of the numerous exits within this place.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u2C61', Color.YELLOW, COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = KeyOnUseBehavior.class;

        entityProtos.add(ep);
        
        /******************************************
         * #6 - FLOOR - TILE
         */
        ep = new EntityProto(ID_FLOOR);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar(' ', Color.WHITE, COLOR_FLOOR);
        ep.entityBaseProto.name = "Floor";
        ep.entityBaseProto.description = "\"If you fall, I'll be there.\" - Floor, 2015\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, COLOR_FLOOR_HIDDEN);

        entityProtos.add(ep);
        
        /******************************************
         * #7 - PLAYER - ACTOR
         */
        ep = new EntityProto(ID_PLAYER);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('@', Color.CYAN, COLOR_FLOOR);
        ep.entityBaseProto.name = "The Player";
        ep.entityBaseProto.description = 
              "That's you, a frightened individual running through a\n"
            + "dungeon, screaming after passing every corner, afraid\n"
            + "of what will happen next.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('@', Color.CYAN, COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = 5;
        ep.actorProto.onMovedBehaviorClass = PlayerOnMovedBehavior.class;
        ep.actorProto.onTickBehaviorClass = PlayerOnTickBehavior.class;
        ep.actorProto.onDamageBehaviorClass = PlayerOnDamageBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = PlayerOnPickedUpItemBehavior.class;
        ep.actorProto.onDroppedItemBehaviorClass = PlayerOnDroppedItemBehavior.class;
        ep.actorProto.onHealBehaviorClass = PlayerOnHealBehavior.class;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 3;

        entityProtos.add(ep);
    
        /******************************************
         * #8 - HEALTH_POTION - ACTOR, ITEM
         */
        ep = new EntityProto(ID_HEALTH_POTION);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\uFBEA', Color.RED, COLOR_FLOOR);
        ep.entityBaseProto.name = "Health Potion";
        ep.entityBaseProto.description = 
              "It heals you, duh.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.isSolid = false;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\uFBEA', Color.RED, COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = HealthPotionOnUseBehavior.class;

        entityProtos.add(ep);
    }
}
