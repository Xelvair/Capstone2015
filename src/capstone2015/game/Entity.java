package capstone2015.game;

import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.MessageBus;

public class Entity {
    public static final int ID_WALL = 0;
    public static final int ID_ENTRY = 1;
    public static final int ID_EXIT = 2;
    public static final int ID_STATIC_OBSTACLE = 3;
    public static final int ID_ENEMY = 4;
    public static final int ID_KEY = 5;
    public static final int ID_FLOOR = 6;
    public static final int ID_PLAYER = 7;
  
    private EntityProto proto;
    protected MessageBus messageBus;
    
    public Entity(int entityId, MessageBus messageBus){
        this.proto = EntityProto.get(entityId);
        this.messageBus = messageBus;
    }
 
    //public boolean isTerminate();
    //public Inventory getInventory();
    public int          getId(){return proto.getId();}
    public boolean      isSolid(){return proto.isSolid();}
    public boolean      isOpaque(){return proto.isOpaque();}
    public int          getVisionRadius(){return proto.getVisionRadius();}
    public EntityAction tick(double timeDelta){
        if(proto.getBehavior() == null){
            return new EntityAction(EntityActionType.NONE);
        } else {
            return proto.getBehavior().onTick(timeDelta, messageBus);
        }
    }
    public TerminalChar getRepresentVisible(){return proto.getRepresentVisible();}
    public TerminalChar getRepresentInvisible(){return proto.getRepresentInvisible();}
    public TerminalChar getRepresentInventory(){return proto.getRepresentInventory();}
}
