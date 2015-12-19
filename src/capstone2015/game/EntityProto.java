package capstone2015.game;

import capstone2015.game.behavior.PlayerBehavior;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;
import java.util.ArrayList;

public class EntityProto {

    private final int id;
    private final boolean isSolid;
    private final boolean isOpaque;
    private final EntityBehavior behavior;
    private final TerminalChar representVisible;
    private final TerminalChar representInvisible;
    private final TerminalChar representInventory;

    public EntityProto(
            int id,
            boolean isSolid,
            boolean isOpaque,
            EntityBehavior behavior,
            TerminalChar representVisible,
            TerminalChar representInvisible,
            TerminalChar representInventory
    ){
        this.id = id;
        this.isSolid = isSolid;
        this.isOpaque = isOpaque;
        this.behavior = behavior;
        this.representVisible = representVisible;
        this.representInvisible = representInvisible;
        this.representInventory = representInventory;
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

    public EntityBehavior getBehavior() {
        return behavior;
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
                null, //behavior
                new TerminalChar(' ', Color.WHITE, Color.WHITE), //representVis
                new TerminalChar(' ', Color.WHITE, Color.WHITE), //representInvis
                new TerminalChar(' ', Color.WHITE, Color.WHITE)  //representInv
        ));
        
        //ID_ENTRY
        entityProtoList.add(new EntityProto(
                Entity.ID_ENTRY,
                false, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar('\u25BC', Color.BLUE, Color.DARK_GRAY), //representVis
                new TerminalChar('\u25BC', Color.BLUE, Color.BLACK), //representInvis
                new TerminalChar('\u25BC', Color.BLUE, Color.BLACK)  //representInv
        ));
        
        //ID_EXIT
        entityProtoList.add(new EntityProto(
                Entity.ID_EXIT,
                false, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar('\u25B2', Color.GREEN, Color.DARK_GRAY), //representVis
                new TerminalChar('\u25B2', Color.GREEN, Color.BLACK), //representInvis
                new TerminalChar('\u25B2', Color.GREEN, Color.BLACK)  //representInv
        ));
        
        //ID_STATIC_OBSTACLE
        entityProtoList.add(new EntityProto(
                Entity.ID_STATIC_OBSTACLE,
                true, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar('\u25CF', Color.WHITE, Color.DARK_GRAY), //representVis
                new TerminalChar('\u25CF', Color.WHITE, Color.BLACK), //representInvis
                new TerminalChar('\u25CF', Color.WHITE, Color.BLACK)  //representInv
        ));
        
        //ID_ENEMY
        entityProtoList.add(new EntityProto(
                Entity.ID_ENEMY,
                false, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar('\u08B0', Color.RED, Color.DARK_GRAY), //representVis
                new TerminalChar('\u08B0', Color.RED, Color.BLACK), //representInvis
                new TerminalChar('\u08B0', Color.RED, Color.BLACK)  //representInv
        ));
        
        //ID_KEY
        entityProtoList.add(new EntityProto(
                Entity.ID_KEY,
                false, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar('\u2C61', Color.YELLOW, Color.DARK_GRAY), //representVis
                new TerminalChar('\u2C61', Color.YELLOW, Color.BLACK), //representInvis
                new TerminalChar('\u2C61', Color.YELLOW, Color.BLACK)  //representInv
        ));
        
        //ID_FLOOR
        entityProtoList.add(new EntityProto(
                Entity.ID_FLOOR,
                false, //isSolid
                false, //isOpaque
                null, //behavior
                new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY), //representVis
                new TerminalChar(' ', Color.WHITE, Color.BLACK),     //representInvis
                new TerminalChar('.', Color.WHITE, Color.DARK_GRAY)  //representInv
        ));
        
        //ID_PLAYER
        entityProtoList.add(new EntityProto(
                Entity.ID_PLAYER,
                false, //isSolid
                false, //isOpaque
                new PlayerBehavior(), //behavior
                new TerminalChar('@', Color.CYAN, Color.DARK_GRAY), //representVis
                new TerminalChar('@', Color.CYAN, Color.BLACK),     //representInvis
                new TerminalChar('@', Color.CYAN, Color.DARK_GRAY)  //representInv
        ));
    }
    
}
