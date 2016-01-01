package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.behavior.*;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.MessageBus;
import java.awt.Color;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.TreeMap;

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
    public static final int ID_SWORD = 9;
    public static final int ID_BOW = 10;
    public static final int ID_ARROW = 11;
    public static final int ID_EFFECT = 99;
    
    public static final Color COLOR_FLOOR = new Color(87,59,12);
    public static final Color COLOR_FLOOR_HIDDEN = new Color(26, 20, 4);
    
    private static TreeMap<Integer, EntityProto> entityProtos;
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

    public static <T> T adaptiveInstantiate(Class<T> clazz, Map<String, Object> instantiationParams){
        /*******************************
         * If no class was passed, don't instantiatie anything
         * hence, adaptive ;)
         */
        if(clazz == null)
            return null;

        /*******************************
         * Check if a ctor exists that takes instantiation parameters
         */
        try{
            Constructor ctor_ip = clazz.getConstructor(Map.class);
            try {
                return (T)ctor_ip.newInstance(instantiationParams);
            } catch(Exception e){
                System.out.println("Instantiation failed for parameterized ctor!");
            }
        } catch(NoSuchMethodException e){
            //Fallthrough
        }

        /*******************************
         * else call default ctor
         */
        try {
            return clazz.newInstance();
        } catch(Exception e){
            System.out.println("Instantiation failed for default ctor!");
        }

        return null;
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

    public static Actor createActor(int entityProtoId, Vec2i pos){
        return createActor(entityProtoId, pos.getX(), pos.getY(), null, null);
    }
    public static Actor createActor(int entityProtoId, Vec2i pos, Map<String, Object> instantiationParams){
        return createActor(entityProtoId, pos.getX(), pos.getY(), instantiationParams, null);
    }
    public static Actor createActor(int entityProtoId, int x, int y){
        return createActor(entityProtoId, x, y, new TreeMap<>(), null);
    }
    public static Actor createActor(int entityProtoId, int x, int y, Map<String, Object> instantiationParams){
        return createActor(entityProtoId, x, y, instantiationParams, null);
    }
    public static Actor createActor(int entityProtoId, Vec2i pos, EntityBase parent){
        return createActor(entityProtoId, pos.getX(), pos.getY(), parent);
    }
    public static Actor createActor(int entityProtoId, Vec2i pos, Map<String, Object> instantiationParams, EntityBase parent){
        return createActor(entityProtoId, pos.getX(), pos.getY(), instantiationParams, parent);
    }
    public static Actor createActor(int entityProtoId, int x, int y, EntityBase parent){
        return createActor(entityProtoId, x, y, new TreeMap<>(), parent);
    }
    public static Actor createActor(int entityProtoId, int x, int y, Map<String, Object> instantiationParams, EntityBase parent){
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

            /***********************************
             * Set members according to proto
             */
            Actor actor = new Actor();
            actor.proto = e_proto;
            actor.messageBus = messageBus;
            actor.pos = new Vec2i(x, y);
            actor.health = e_proto.actorProto.maxHealth;
            actor.visionRadius = e_proto.actorProto.visionRadius;

            if(parent != null)
                actor.parent = parent;

            /***********************************
             * Set members according to instantiation params
             */
            if(instantiationParams.containsKey("Duration"))
                actor.duration = (double) instantiationParams.get("Duration");
            if(instantiationParams.containsKey("Health"))
                actor.health = (Integer) instantiationParams.get("Health");
            if(instantiationParams.containsKey("Inventory"))
                actor.inventory = (Inventory) instantiationParams.get("Inventory");

            /***********************************
             * Load behaviors
             */
            actor.onMovedBehavior = adaptiveInstantiate(e_proto.actorProto.onMovedBehaviorClass, instantiationParams);
            actor.onTickBehavior = adaptiveInstantiate(e_proto.actorProto.onTickBehaviorClass, instantiationParams);
            actor.onWalkedOverBehavior = adaptiveInstantiate(e_proto.mapEntityProto.onWalkedOverBehaviorClass, instantiationParams);
            actor.onDamageBehavior = adaptiveInstantiate(e_proto.actorProto.onDamageBehaviorClass, instantiationParams);
            actor.onPickedUpItemBehavior = adaptiveInstantiate(e_proto.actorProto.onPickedUpItemBehaviorClass, instantiationParams);
            actor.onDroppedItemBehavior = adaptiveInstantiate(e_proto.actorProto.onDroppedItemBehaviorClass, instantiationParams);
            actor.onHealBehavior = adaptiveInstantiate(e_proto.actorProto.onHealBehaviorClass, instantiationParams);

            /***********************************
             * If the actor has inventory space, instantiate an inventory
             */
            if(e_proto.actorProto.inventorySize > 0 && actor.inventory == null)
                actor.inventory = new Inventory(e_proto.actorProto.inventorySize);

            /***********************************
             * At last, call the onInstantiationFunction to let the entity do its own thing
             * with the custom data passed over instantiationParams
             */
            if(e_proto.actorProto.onInstantiationFunction != null)
                e_proto.actorProto.onInstantiationFunction.accept(actor, instantiationParams);
        
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

            /***********************************
             * Set members according to proto
             */
            Item item = new Item();
            item.proto = e_proto;
            item.messageBus = messageBus;

            /***********************************
             * Load behaviors
             */
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
        entityProtos = new TreeMap<>();
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
        ep.mapEntityProto.solidType = SolidType.SOLID;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, new Color(46,47,45));
        
        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u25BC', Color.BLUE, COLOR_FLOOR_HIDDEN);

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.SOLID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u25B2', Color.GREEN, COLOR_FLOOR_HIDDEN);

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.FLUID;
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
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u08B0', new Color(0, 153, 76), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = 5;
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = MovingDamageOnCollisionOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 14;
        ep.actorProto.visionRevealedByDefault = true;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.FLUID;
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
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = KeyOnUseBehavior.class;

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.GHOST;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, COLOR_FLOOR_HIDDEN);

        entityProtos.put(ep.id, ep);
        
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
        ep.mapEntityProto.solidType = SolidType.FLUID; //TODO: CHANGE TO NORMAL AND ADAPT ASTAR
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('@', Color.CYAN, COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = 5;
        ep.actorProto.onMovedBehaviorClass = PlayerOnMovedBehavior.class;
        ep.actorProto.onTickBehaviorClass = PlayerOnTickBehavior.class;
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = PlayerOnPickedUpItemBehavior.class;
        ep.actorProto.onDroppedItemBehaviorClass = PlayerOnDroppedItemBehavior.class;
        ep.actorProto.onHealBehaviorClass = PlayerOnHealBehavior.class;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 5;
        ep.actorProto.teamId = ActorProto.TEAM_PLAYER;

        entityProtos.put(ep.id, ep);
    
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
        ep.mapEntityProto.solidType = SolidType.FLUID;
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
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = HealthPotionOnUseBehavior.class;

        entityProtos.put(ep.id, ep);

        /*********************************************
         * #9 - SWORD - ACTOR, ITEM
         */

        ep = new EntityProto(ID_SWORD);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();

        ep.entityBaseProto.represent = new TerminalChar('\u019A', new Color(160, 160, 160), COLOR_FLOOR);
        ep.entityBaseProto.name = "Sword";
        ep.entityBaseProto.description =
                "Swing it at your foes to deal damage1";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u019A', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = SwordOnUseBehavior.class;

        entityProtos.put(ep.id, ep);

        /*********************************************
         * #10 - BOW - ACTOR, ITEM
         */

        ep = new EntityProto(ID_BOW);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();

        ep.entityBaseProto.represent = new TerminalChar(')', new Color(160, 160, 160), COLOR_FLOOR);
        ep.entityBaseProto.name = "Bow";
        ep.entityBaseProto.description =
                "Hurls arrows towards your enemies, provided you have any.\n\n"
              + "Arrows, that is. You'll have plenty of enemies alright.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(')', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = BowOnUseBehavior.class;

        entityProtos.put(ep.id, ep);

        /*********************************************
         * #11 - ARROW - ACTOR, ITEM
         */

        ep = new EntityProto(ID_ARROW);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();

        ep.entityBaseProto.represent = new TerminalChar('I', new Color(160, 160, 160), COLOR_FLOOR);
        ep.entityBaseProto.name = "Arrow";
        ep.entityBaseProto.description =
                "Used as ammunition for the bow. After the arrow strikes its target,\n"
              + "you will be able to pick it up and shoot it again";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('I', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = ArrowOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = null;

        entityProtos.put(ep.id, ep);

        /*********************************************
         * #99 - EFFECT - ACTOR
         */

        ep = new EntityProto(ID_EFFECT);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();

        ep.entityBaseProto.represent = new TerminalChar();
        ep.entityBaseProto.name = "Effect";
        ep.entityBaseProto.description =
                "You should not be able to read this.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.GHOST;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar();
        ep.actorProto.onInstantiationFunction = (Actor actor, Map<String, Object> params) -> {
            TerminalChar represent_override = (TerminalChar)params.get("RepresentOverride");
            actor.setRepresentOverride(represent_override);
        };
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = DurationOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;

        entityProtos.put(ep.id, ep);
    }
}
