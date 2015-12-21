package capstone2015.game;

import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.MessageBus;
import java.util.HashMap;

public class Entity {
    public static final int ID_WALL = 0;
    public static final int ID_ENTRY = 1;
    public static final int ID_EXIT = 2;
    public static final int ID_STATIC_OBSTACLE = 3;
    public static final int ID_ENEMY = 4;
    public static final int ID_KEY = 5;
    public static final int ID_FLOOR = 6;
    public static final int ID_PLAYER = 7;
  
    protected EntityProto   proto;
    protected MessageBus    messageBus;
    private boolean         terminate = false;
    
    public Entity(int entityId, MessageBus messageBus){
        this.proto = EntityProto.get(entityId);
        this.messageBus = messageBus;
    }
 
    //public boolean isTerminate();
    //public Inventory getInventory();
    public void                             terminate(){terminate = true;}
    public boolean                          isTerminate(){return terminate;}
    public int                              getId(){return proto.getId();}
    public boolean                          isSolid(){return proto.isSolid();}
    public boolean                          isOpaque(){return proto.isOpaque();}
    public int                              getVisionRadius(){return proto.getVisionRadius();}
    public OnTickBehavior                   getOnTickBehavior(){return null;}
    public OnDamageBehavior                 getOnDamageBehavior(){return null;}
    public int                              getHealthPoints(){return proto.getHealthPoints();}
    public boolean                          isInvincible(){return proto.getHealthPoints() >= 0;}
    public TerminalChar                     getRepresentVisible(){return proto.getRepresentVisible();}
    public TerminalChar                     getRepresentInvisible(){return proto.getRepresentInvisible();}
    public TerminalChar                     getRepresentInventory(){return proto.getRepresentInventory();}
    public HashMap<ActiveEntity, Double>    getDamageIgnoreTimers(){return null;}
}
