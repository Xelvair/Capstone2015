package capstone2015.entity;

import capstone2015.game.Inventory;
import capstone2015.game.behavior.*;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.MessageBus;
import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    /*******************************
     * ENTITY IDS
     */
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
    public static final int ID_WATER = 12;
    public static final int ID_WOOD_FLOOR = 13;
    public static final int ID_FAKE_WALL = 14;
    public static final int ID_MAGIC_WAND = 15;
    public static final int ID_MAGIC_BOLT = 16;
    public static final int ID_FERN = 17;
    public static final int ID_SOIL = 18;
    public static final int ID_FIRE_IMP = 19;
    public static final int ID_FIRE_BOLT = 20;
    public static final int ID_TAMING_SCROLL = 21;
    public static final int ID_TAMING_SPELL = 22;
    public static final int ID_TITAN = 23;
    public static final int ID_METEOR = 24;
    public static final int ID_EFFECT = 99;
    
    /*******************************
     * ENTITY COLORS
     */
    
    public static final Color COLOR_SOIL = new Color(56,34,8);
    public static final Color COLOR_SOIL_HIDDEN = new Color(20,15,3);
    public static final Color COLOR_FLOOR = new Color(87,59,12);
    public static final Color COLOR_FLOOR_HIDDEN = new Color(26, 20, 4);
    
    /*******************************
     * SHADER_TYPES
     */
    public static final int SHADER_NONE = 0;
    public static final int SHADER_COLOR_VARIATION = 1;
    public static final int SHADER_WATER = 2;
    public static final int SHADER_BONFIRE = 3;
    public static final int SHADER_COUNT = 4;
    
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

    public static <T> T adaptiveInstantiate(Class<T> clazz, Actor actor, Map<String, Object> instantiationParams){
        /*******************************
         * If no class was passed, don't instantiatie anything
         * hence, adaptive ;)
         */
        if(clazz == null)
            return null;

        /*******************************
         * Check if a ctor exists that takes the actor and instantiation parameters
         */
        try{
            Constructor ctor_ip = clazz.getConstructor(Actor.class, Map.class);
            try {
                return (T)ctor_ip.newInstance(actor, instantiationParams);
            } catch(InstantiationException e){
                System.out.println("Instantiation failed for parameterized ctor! InstantiationException" + e.getMessage());
            } catch(IllegalAccessException e){
                System.out.println("Instantiation failed for parameterized ctor! IllegalAccessException" + e.getMessage());
            } catch(InvocationTargetException e){
                System.out.println("Instantiation failed for parameterized ctor! InvocationTargetException" + e.getCause());
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
            System.out.println("Instantiation failed for default ctor!" + e.getMessage());
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
        actor.health = actor.getMaxHealth();
        actor.visionRadius = e_proto.actorProto.visionRadius;

        if(parent != null)
            actor.parent = parent;

        /***********************************
         * Set members according to instantiation params
         */
        if(instantiationParams != null){
            if(instantiationParams.containsKey("Duration"))
                actor.duration = (double) instantiationParams.get("Duration");
            if(instantiationParams.containsKey("Health"))
                actor.health = (Integer) instantiationParams.get("Health");
            if(instantiationParams.containsKey("Inventory"))
                actor.inventory = (Inventory) instantiationParams.get("Inventory");
            if(instantiationParams.containsKey("TeamIdOverride"))
                actor.setTeamIdOverride((int)instantiationParams.get("TeamIdOverride"));
            if(instantiationParams.containsKey("Level"))
                actor.setLevel((int)instantiationParams.get("Level"));
            if(instantiationParams.containsKey("RepresentOverride"))
                actor.setRepresentOverride((TerminalChar)instantiationParams.get("RepresentOverride"));
        }

        /***********************************
         * Load behaviors
         */
        actor.onMovedBehavior = adaptiveInstantiate(e_proto.actorProto.onMovedBehaviorClass, actor, instantiationParams);
        actor.onTickBehavior = adaptiveInstantiate(e_proto.actorProto.onTickBehaviorClass, actor, instantiationParams);
        actor.onWalkedOverBehavior = adaptiveInstantiate(e_proto.mapEntityProto.onWalkedOverBehaviorClass, actor, instantiationParams);
        actor.onDamageBehavior = adaptiveInstantiate(e_proto.actorProto.onDamageBehaviorClass, actor, instantiationParams);
        actor.onPickedUpItemBehavior = adaptiveInstantiate(e_proto.actorProto.onPickedUpItemBehaviorClass, actor, instantiationParams);
        actor.onDroppedItemBehavior = adaptiveInstantiate(e_proto.actorProto.onDroppedItemBehaviorClass, actor, instantiationParams);
        actor.onHealBehavior = adaptiveInstantiate(e_proto.actorProto.onHealBehaviorClass, actor, instantiationParams);
        actor.onTamedBehavior = adaptiveInstantiate(e_proto.actorProto.onTamedBehaviorClass, actor, instantiationParams);

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
        ep.mapEntityProto.shaderType = SHADER_COLOR_VARIATION;
        
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
        ep.mapEntityProto.shaderType = SHADER_NONE;

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
        ep.mapEntityProto.shaderType = SHADER_NONE;

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
        ep.mapEntityProto.shaderType = SHADER_BONFIRE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = DamageOnCollisionOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #4 - RATTLESNAKE - ACTOR
         */
        ep = new EntityProto(ID_RATTLESNAKE);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('S', new Color(0, 153, 76), COLOR_FLOOR);
        ep.entityBaseProto.name = "Rattlesnake";
        ep.entityBaseProto.description = 
              "This dangerous creature can become very deadly \n"
            + "very quick if you come too close to it. Keep your\n"
            + "distance, or be prepared for a fight.\n\n"
            + "Common Creature\n"
            + "Damage: 1\n"
            + "Min. Tame Chance: 50%\n"
            + "Max. Tame Chance: 100%";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('S', new Color(0, 153, 76), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = new Integer[]{5, 15, 30};
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = RattlesnakeOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = DefaultOnTamedBehavior.class;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.visionRevealedByDefault = true;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.5f;
        ep.actorProto.tameMaxChance = 1.f;
        ep.actorProto.outerStray = 12.d;
        ep.actorProto.innerStray = 5.d;
        ep.actorProto.attackDamage = new Integer[]{1, 2, 3};
        ep.actorProto.attackTimeout = 0.5d;
        ep.actorProto.getInRangeMoveTimeout = 0.175d;
        ep.actorProto.attackMoveTimeout = new Double[]{0.225d, 0.175d, 0.125d};
        ep.actorProto.wanderingMoveTimeout = 2.d;
        ep.actorProto.attackRange = 1;

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
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
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
        ep.mapEntityProto.shaderType = SHADER_COLOR_VARIATION;

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
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('@', Color.CYAN, COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = 10;
        ep.actorProto.onMovedBehaviorClass = PlayerOnMovedBehavior.class;
        ep.actorProto.onTickBehaviorClass = PlayerOnTickBehavior.class;
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = PlayerOnPickedUpItemBehavior.class;
        ep.actorProto.onDroppedItemBehaviorClass = PlayerOnDroppedItemBehavior.class;
        ep.actorProto.onHealBehaviorClass = PlayerOnHealBehavior.class;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 12;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 7;
        ep.actorProto.teamId = ActorProto.TEAM_PLAYER;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

        entityProtos.put(ep.id, ep);
    
        /******************************************
         * #8 - HEALTH_POTION - ACTOR, ITEM
         */
        ep = new EntityProto(ID_HEALTH_POTION);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();
        
        ep.entityBaseProto.represent = new TerminalChar('!', Color.RED, COLOR_FLOOR);
        ep.entityBaseProto.name = "Health Potion";
        ep.entityBaseProto.description = 
              "Upon drinking this potion, you will instantly be\n"
            + "healed for 3 HP.\n"
            + "If you use this potion on one of your followers,\n"
            + "it will heal them to their full HP.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('!', Color.RED, COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
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
                "Swing it at your foes to deal damage.\n\n"
                + "Common Weapon\n"
                + "Damage: 5\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u019A', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
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
                "Hurls arrows towards your enemies, provided you have any.\n"
              + "Arrows, that is. You'll have plenty of enemies alright.\n\n"
              + "Common Weapon\n"
              + "Damage: 3\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(')', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
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

        ep.entityBaseProto.represent = new TerminalChar('\u2192', new Color(160, 160, 160), COLOR_FLOOR);
        ep.entityBaseProto.name = "Arrow";
        ep.entityBaseProto.description =
                "Used as ammunition for the bow. After the arrow strikes its target,\n"
              + "you will be able to pick it up and shoot it again.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u2192', new Color(160, 160, 160), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = ArrowOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = null;

        entityProtos.put(ep.id, ep);

        /******************************************
         * #12 - WATER - TILE
         */
        ep = new EntityProto(ID_WATER);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar('~', new Color(70, 140, 255), new Color(40, 80, 255));
        ep.entityBaseProto.name = "Water";
        ep.entityBaseProto.description = ""
                + "You cannot pass water, seems like you haven't\n"
                + "been attending your swimming lessons.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('~', new Color(35, 70, 120), new Color(20, 40, 120));
        ep.mapEntityProto.shaderType = SHADER_WATER;

        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #13 - WOOD_FLOOR - TILE
         */
        ep = new EntityProto(ID_WOOD_FLOOR);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar('┼', new Color(70, 35, 0), new Color(100, 50, 0));
        ep.entityBaseProto.name = "Wood Floor";
        ep.entityBaseProto.description = ""
                + "Exactly like the other kind of floor, but wooden.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.GHOST;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('┼', new Color(25, 10, 5), new Color(35, 18, 7));
        ep.mapEntityProto.shaderType = SHADER_COLOR_VARIATION;

        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #14 - FAKE_WALL - TILE
         */
        ep = new EntityProto(ID_FAKE_WALL);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar(' ', Color.WHITE, new Color(139, 141, 122));
        ep.entityBaseProto.name = "Fake Wall";
        ep.entityBaseProto.description = 
              "Looks just like a real wall, but lets you pass through.";
        ep.mapEntityProto.isOpaque = true;
        ep.mapEntityProto.solidType = SolidType.GHOST;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, new Color(46,47,45));
        ep.mapEntityProto.shaderType = SHADER_COLOR_VARIATION;
        
        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #15 - MAGIC_WAND - ACTOR, ITEM
         */

        ep = new EntityProto(ID_MAGIC_WAND);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();

        ep.entityBaseProto.represent = new TerminalChar('/', new Color(0, 0, 255), COLOR_FLOOR);
        ep.entityBaseProto.name = "Magic Wand";
        ep.entityBaseProto.description =
                "Shoots magic bolts at your enemies. The bolts will home\n"
              + "in on your target.\n\n"
              + "Magic Weapon\n"
              + "Damage: 1\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('/', new Color(0, 0, 255), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = MagicWandOnUseBehavior.class;

        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #16 - MAGIC_BOLT - ACTOR
         */

        ep = new EntityProto(ID_MAGIC_BOLT);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();

        ep.entityBaseProto.represent = new TerminalChar('*', new Color(30, 30, 255), COLOR_FLOOR);
        ep.entityBaseProto.name = "Magic Bolt";
        ep.entityBaseProto.description =
                "Homes in on its target and deals damage.\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('*', new Color(30, 30, 255), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = MagicBoltOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #17 - FERN - TILE
         */
        ep = new EntityProto(ID_FERN);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar('\u03D2', new Color(0, 110, 40), COLOR_SOIL);
        ep.entityBaseProto.name = "Fern";
        ep.entityBaseProto.description = 
                  "It obstructs your view. But you're willing to tolerate\n"
                + "that disadvantage because you're someone who cares\n"
                + "about the biologic stability of this place.";
        ep.mapEntityProto.isOpaque = true;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('\u03D2', new Color(0, 75, 40), COLOR_SOIL_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        
        entityProtos.put(ep.id, ep);
        /******************************************
         * #18 - SOIL - TILE
         */
        ep = new EntityProto(ID_SOIL);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.tileProto = new TileProto();
        
        ep.entityBaseProto.represent = new TerminalChar(' ', Color.WHITE, COLOR_SOIL);
        ep.entityBaseProto.name = "Soil";
        ep.entityBaseProto.description = 
                  "This seems like a nice place to grow plants.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', Color.WHITE, COLOR_SOIL_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_COLOR_VARIATION;
        
        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #19 - FIRE_IMP - ACTOR
         */
        ep = new EntityProto(ID_FIRE_IMP);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('F', new Color(226, 88, 34), COLOR_FLOOR);
        ep.entityBaseProto.name = "Fire Imp";
        ep.entityBaseProto.description = 
              "An imp who made its way all the way from hell\n"
            + "to end up in this dungeon. In his frustration, it\n"
            + "has decided to unleash a fiery inferno on anyone\n"
            + "who dares enter his vincinity.\n\n"
            + "Common Creature\n"
            + "Damage: 1\n"
            + "Min. Tame Chance: 10%\n"
            + "Max. Tame Chance: 50%\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('F', new Color(226, 88, 34), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = new Integer[]{8, 20};
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = FireImpOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = DefaultOnTamedBehavior.class;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.visionRevealedByDefault = true;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.1f;
        ep.actorProto.tameMaxChance = 0.5f;
        ep.actorProto.outerStray = 15.d;
        ep.actorProto.innerStray = 7.d;
        ep.actorProto.attackDamage = 0; //Imp doesnt do damage directly
        ep.actorProto.attackTimeout = new Double[]{0.65d, 0.325d, 0.1d};
        ep.actorProto.getInRangeMoveTimeout = 0.175d;
        ep.actorProto.attackMoveTimeout = new Double[]{0.75d, 0.25d};
        ep.actorProto.wanderingMoveTimeout = 2.d;
        ep.actorProto.attackRange = 7;

        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #20 - FIRE_BOLT - ACTOR
         */

        ep = new EntityProto(ID_FIRE_BOLT);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();

        ep.entityBaseProto.represent = new TerminalChar('*', new Color(226, 88, 34), COLOR_FLOOR);
        ep.entityBaseProto.name = "Fire Bolt";
        ep.entityBaseProto.description =
                "Fired by Imps.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('*', new Color(226, 88, 34), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = FireBoltOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #21 - TAMING_SCROLL - ACTOR, ITEM
         */

        ep = new EntityProto(ID_TAMING_SCROLL);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        ep.itemProto = new ItemProto();

        ep.entityBaseProto.represent = new TerminalChar('I', new Color(0, 162, 232), COLOR_FLOOR);
        ep.entityBaseProto.name = "Taming Scroll";
        ep.entityBaseProto.description =
                "Attempts to tame a creature and make it fight for you.\n"
              + "The taming process can fail, the lower the tamed \n"
              + "creatures HP, the more likely it is that its taming will\n"
              + "be successfull. Upon taming a creature, it regains\n"
              + "its max HP, and its power level is raised, effectively\n"
              + "making it stronger than the rest of its type.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('I', new Color(0, 162, 232), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = null;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = true;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;
        ep.itemProto.onItemPickedUpBehaviorClass = null;
        ep.itemProto.onItemDroppedBehaviorClass = null;
        ep.itemProto.onUseBehaviorClass = TamingScrollOnUseBehavior.class;
        
        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #22 - TAMING_SPELL - ACTOR
         */

        ep = new EntityProto(ID_TAMING_SPELL);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();

        ep.entityBaseProto.represent = new TerminalChar('>', new Color(0, 162, 232), COLOR_FLOOR);
        ep.entityBaseProto.name = "Taming Spell";
        ep.entityBaseProto.description =
                "Attempts to tame a creature.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('>', new Color(0, 162, 232), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = TamingSpellOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 1;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

        entityProtos.put(ep.id, ep);
        
        /******************************************
         * #23 - TITAN - ACTOR
         */
        ep = new EntityProto(ID_TITAN);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();
        
        ep.entityBaseProto.represent = new TerminalChar('T', new Color(0, 0, 50), COLOR_FLOOR);
        ep.entityBaseProto.name = "Titan";
        ep.entityBaseProto.description = 
            "A strong and frightening guardian of the dungeon.\n\n" +
            "Rare Creature\n" +
            "Damage: 3\n" +
            "Min. Tame Chance: 1%\n" +
            "Max. Tame Chance: 33%\n";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.NORMAL;
        ep.mapEntityProto.isEncounterNotified = false;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar('T', new Color(0, 0, 50), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = new Integer[]{50, 80, 120};
        ep.actorProto.onDamageBehaviorClass = DefaultOnDamageBehavior.class;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = TitanOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = DefaultOnTamedBehavior.class;
        ep.actorProto.visionRadius = 10;
        ep.actorProto.visionRevealedByDefault = true;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.01f;
        ep.actorProto.tameMaxChance = 0.33f;
        ep.actorProto.outerStray = 12.d;
        ep.actorProto.innerStray = 5.d;
        ep.actorProto.attackDamage = new Integer[]{3, 7, 15};
        ep.actorProto.attackTimeout = .5d;
        ep.actorProto.getInRangeMoveTimeout = 0.175d;
        ep.actorProto.attackMoveTimeout = new Double[]{0.45d, 0.225d, 0.125d};
        ep.actorProto.wanderingMoveTimeout = 2.d;
        ep.actorProto.attackRange = 1;

        entityProtos.put(ep.id, ep);
        
        /*********************************************
         * #24 - METEOR - ACTOR
         */

        ep = new EntityProto(ID_METEOR);
        ep.entityBaseProto = new EntityBaseProto();
        ep.mapEntityProto = new MapEntityProto();
        ep.actorProto = new ActorProto();

        ep.entityBaseProto.represent = new TerminalChar(' ', new Color(255, 0, 0), COLOR_FLOOR);
        ep.entityBaseProto.name = "Titan's Meteor";
        ep.entityBaseProto.description =
                "Rains down on you and deals damage!.";
        ep.mapEntityProto.isOpaque = false;
        ep.mapEntityProto.solidType = SolidType.FLUID;
        ep.mapEntityProto.isEncounterNotified = true;
        ep.mapEntityProto.onWalkedOverBehaviorClass = null;
        ep.mapEntityProto.representInvisible = new TerminalChar(' ', new Color(255, 0, 0), COLOR_FLOOR_HIDDEN);
        ep.mapEntityProto.shaderType = SHADER_NONE;
        ep.actorProto.maxHealth = -1;
        ep.actorProto.onMovedBehaviorClass = null;
        ep.actorProto.onTickBehaviorClass = MeteorOnTickBehavior.class;
        ep.actorProto.onPickedUpItemBehaviorClass = null;
        ep.actorProto.onDroppedItemBehaviorClass = null;
        ep.actorProto.onHealBehaviorClass = null;
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_DUNGEON;
        ep.actorProto.tameMinChance = 0.f;
        ep.actorProto.tameMaxChance = 0.f;

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
        ep.mapEntityProto.shaderType = SHADER_NONE;
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
        ep.actorProto.onTamedBehaviorClass = null;
        ep.actorProto.visionRadius = 0;
        ep.actorProto.visionRevealedByDefault = false;
        ep.actorProto.pickupable = false;
        ep.actorProto.inventorySize = 0;
        ep.actorProto.teamId = ActorProto.TEAM_NONE;

        entityProtos.put(ep.id, ep);
    }
}
