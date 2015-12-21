package capstone2015.game;

import capstone2015.game.behavior.DamageOnCollisionOnTickBehavior;
import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnMovedBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.game.behavior.PlayerOnDamageBehavior;
import capstone2015.game.behavior.PlayerOnMovedBehavior;
import capstone2015.game.behavior.PlayerOnTickBehavior;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;
import java.util.ArrayList;

public class EntityProto {

    private final int id;
    private final boolean isSolid;
    private final boolean isOpaque;
    private final boolean isEncounterNotified;
    private final int visionRadius;
    private final int healthPoints;
    private final Class<? extends OnTickBehavior> onTickBehaviorClass;
    private final Class<? extends OnDamageBehavior> onDamageBehaviorClass;
    private final Class<? extends OnMovedBehavior> onMovedBehaviorClass;
    private final TerminalChar representVisible;
    private final TerminalChar representInvisible;
    private final TerminalChar representInventory;
    private final String name;
    private final String description;

    public EntityProto(
            int id,
            boolean isSolid,
            boolean isOpaque,
            boolean isEncounterNotified,
            int visionRadius,
            int healthPoints,
            Class<? extends OnTickBehavior> onTickBehaviorClass,
            Class<? extends OnDamageBehavior> onDamageBehaviorClass,
            Class<? extends OnMovedBehavior> onMovedBehaviorClass,
            TerminalChar representVisible,
            TerminalChar representInvisible,
            TerminalChar representInventory,
            String name,
            String description
    ){
        this.id = id;
        this.isSolid = isSolid;
        this.isOpaque = isOpaque;
        this.isEncounterNotified = isEncounterNotified;
        this.visionRadius = visionRadius;
        this.healthPoints = healthPoints;
        this.onTickBehaviorClass = onTickBehaviorClass;
        this.onDamageBehaviorClass = onDamageBehaviorClass;
        this.onMovedBehaviorClass = onMovedBehaviorClass;
        this.representVisible = representVisible;
        this.representInvisible = representInvisible;
        this.representInventory = representInventory;
        this.name = name;
        this.description = description;
    }
    
    public int getId(){
        return id;
    }
    
    public boolean isOpaque() {
        return isOpaque;
    }

    public boolean isSolid() {
        return isSolid;
    }
    
    public boolean isEncounterNotified(){
        return isEncounterNotified;
    }
    
    public int getVisionRadius(){
        return visionRadius;
    }
    
    public int getHealthPoints(){
        return healthPoints;
    }

    public OnTickBehavior createOnTickBehavior() {
        if(onTickBehaviorClass == null){
            return null;
        }
        try{
            return onTickBehaviorClass.newInstance();
        } catch(Exception e){
            System.out.println("Failed to instantiate OnTickBehavior class.");
            return null;
        }
    }

    public OnDamageBehavior createOnDamageBehavior() {
        if(onDamageBehaviorClass == null){
            return null;
        }
        try{
            return onDamageBehaviorClass.newInstance();
        } catch(Exception e){
            System.out.println("Failed to instantiate OnTickBehavior class.");
            return null;
        }
    }
    
    public OnMovedBehavior createOnMovedBehavior() {
        if(onMovedBehaviorClass == null){
            return null;
        }
        try{
            return onMovedBehaviorClass.newInstance();
        } catch(Exception e){
            System.out.println("Failed to instantiate OnTickBehavior class.");
            return null;
        }
    }
    
    public TerminalChar getRepresentVisible() {
        return representVisible;
    }

    public TerminalChar getRepresentInvisible() {
        return representInvisible;
    }

    public TerminalChar getRepresentInventory() {
        return representInventory;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        return description;
    }
    
    /**************** STATIC METHODS ***********************/
    static ArrayList<EntityProto> entityProtoList;
    
    public static EntityProto get(int entityId){
        if(entityProtoList == null){
            initEntityProto();
        }
        return entityProtoList.get(entityId);
    }
    
    private static void initEntityProto() {
        entityProtoList = new ArrayList<>();
        
        //ID_WALL
        entityProtoList.add(new EntityProto(
                Entity.ID_WALL,
                true, //isSolid
                true, //isOpaque
                false, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                null, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar(' ', Color.WHITE, Color.WHITE), //representVis
                new TerminalChar(' ', Color.WHITE, new Color(20, 20, 20)), //representInvis
                new TerminalChar(' ', Color.WHITE, Color.WHITE), //representInv
                "Wall", //name
                  "Walls make up most of the dungeon. You can not\n"
                + "walk over them, you can not see past them, and they\n"
                + "stop you from running away from your foes.\n"
                + "Be very wary of these strange creatures."
        ));
        
        //ID_ENTRY
        entityProtoList.add(new EntityProto(
                Entity.ID_ENTRY,
                false, //isSolid
                false, //isOpaque
                true, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                null, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar('\u25BC', Color.BLUE, Color.DARK_GRAY), //representVis
                new TerminalChar('\u25BC', Color.BLUE, new Color(10, 10, 10)), //representInvis
                new TerminalChar('\u25BC', Color.BLUE, Color.BLACK), //representInv
                "Entry", //name
                  "The place where you entered the dungeon.\n"
                + "There's no turning back now!"
        ));
        
        //ID_EXIT
        entityProtoList.add(new EntityProto(
                Entity.ID_EXIT,
                false, //isSolid
                false, //isOpaque
                true, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                null, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar('\u25B2', Color.GREEN, Color.DARK_GRAY), //representVis
                new TerminalChar('\u25B2', Color.GREEN, new Color(10, 10, 10)), //representInvis
                new TerminalChar('\u25B2', Color.GREEN, Color.BLACK), //representInv
                "Exit", //name
                  "You need to find this exit in order to leave the\n"
                + "dungeon. However, you cannot leave until you have\n"
                + "found a dungeon key."
        ));
        
        //ID_STATIC_OBSTACLE
        entityProtoList.add(new EntityProto(
                Entity.ID_STATIC_OBSTACLE,
                false, //isSolid
                false, //isOpaque
                false, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                DamageOnCollisionOnTickBehavior.class, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar('\u0751', new Color(255, 140, 0), Color.DARK_GRAY), //representVis
                new TerminalChar('\u0751', new Color(255, 140, 0) , new Color(10, 10, 10)), //representInvis
                new TerminalChar('\u0751', new Color(255, 140, 0), Color.BLACK),  //representInv
                "Bonfire", //name
                  "A warm and cozy bonfire.\n"
                + "Don't even think about stepping on it, it will burn you!"

        ));
        
        //ID_ENEMY
        entityProtoList.add(new EntityProto(
                Entity.ID_ENEMY,
                false, //isSolid
                false, //isOpaque
                false, //isEncounterNotified
                0, //visionRadius
                10, //healthPoints
                DamageOnCollisionOnTickBehavior.class, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar('\u08B0', new Color(85, 107, 47), Color.DARK_GRAY), //representVis
                new TerminalChar('\u08B0', new Color(85, 107, 47), new Color(10, 10, 10)), //representInvis
                new TerminalChar('\u08B0', new Color(85, 107, 47), Color.BLACK), //representInv
                "Rattlesnake", //name
                  "This dangerous creature can become very deadly \n"
                + "very quick if you come too close to it. Keep your\n"
                + "distance, or be prepared for a fight."
        ));
        
        //ID_KEY
        entityProtoList.add(new EntityProto(
                Entity.ID_KEY,
                false, //isSolid
                false, //isOpaque
                true, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                null, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar('\u2C61', Color.YELLOW, Color.DARK_GRAY), //representVis
                new TerminalChar('\u2C61', Color.YELLOW, new Color(10, 10, 10)), //representInvis
                new TerminalChar('\u2C61', Color.YELLOW, Color.BLACK),  //representInv
                "Dungeon Key", //name
                  "This is the key you need to find in order to leave\n"
                + "this dungeon. Once you have found this key, it will show\n"
                + "up in your inventory, and you will be able to leave\n"
                + "through any of the numerous exits within this place."
        ));
        
        //ID_FLOOR
        entityProtoList.add(new EntityProto(
                Entity.ID_FLOOR,
                false, //isSolid
                false, //isOpaque
                false, //isEncounterNotified
                0, //visionRadius
                -1, //healthPoints
                null, //onTickBehavior
                null, //onDamageBehavior
                null, //onMovedBehavior
                new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY), //representVis
                new TerminalChar(' ', Color.WHITE, new Color(10, 10, 10)),     //representInvis
                new TerminalChar('.', Color.WHITE, Color.DARK_GRAY), //representInv
                "Floor", //name
                  "\"If you fall, I'll be there.\" - Floor, 2015\n"
        ));
        
        //ID_PLAYER
        entityProtoList.add(new EntityProto(
                Entity.ID_PLAYER,
                false, //isSolid
                false, //isOpaque
                false, //isEncounterNotified
                10, //visionRadius
                10, //healthPoints
                PlayerOnTickBehavior.class, //onTickBehavior
                PlayerOnDamageBehavior.class, //onDamageBehavior
                PlayerOnMovedBehavior.class, //onMovedBehavior
                new TerminalChar('@', Color.CYAN, Color.DARK_GRAY), //representVis
                new TerminalChar('@', Color.CYAN, Color.BLACK),     //representInvis
                new TerminalChar('@', Color.CYAN, Color.DARK_GRAY), //representInv
                "The Player", //name
                  "That's you, a frightened individual running through a\n"
                + "dungeon, screaming after passing every corner, afraid\n"
                + "of what will happen next."
        ));
    }
    
}

/*
                   "These boulders are very heavy. Good thing you're \n"
                + "a strong independent adventurer, who has - prior to \n"
                + "signing up to this dungeon exploration mission - \n"
                + "been lifting very heavy weights. You should be able to \n"
                + "move a boulder around, as long as there is nothing else\n"
                + "in front of it that might block it."
 */
