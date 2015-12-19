package capstone2015.game;

import capstone2015.graphics.TerminalChar;

public class Entity {
    public static final int ID_WALL = 0;
    public static final int ID_ENTRY = 1;
    public static final int ID_EXIT = 2;
    public static final int ID_STATIC_OBSTACLE = 3;
    public static final int ID_ENEMY = 4;
    public static final int ID_KEY = 5;
    public static final int ID_FLOOR = 6;
  
    private EntityProto proto;
    
    public Entity(int entityId){
        this.proto = EntityProto.get(entityId);
    }
 
    //public boolean isTerminate();
    //public Inventory getInventory();
    public boolean      isSolid(){return proto.isSolid();}
    public boolean      isOpaque(){return proto.isOpaque();}
    public EntityAction tick(){
        if(proto.getBehavior() == null){
            return new EntityAction(EntityActionType.NONE);
        } else {
            return proto.getBehavior().onTick();
        }
    }
    public TerminalChar getRepresentVisible(){return proto.getRepresentVisible();}
    public TerminalChar getRepresentInvisible(){return proto.getRepresentInvisible();}
    public TerminalChar getRepresentInventory(){return proto.getRepresentInventory();}
}
