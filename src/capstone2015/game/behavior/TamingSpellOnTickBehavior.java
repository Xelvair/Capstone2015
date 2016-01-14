package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.AttemptTameParams;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.AttemptTame;
import capstone2015.util.Util;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TamingSpellOnTickBehavior implements OnTickBehavior{
    
    public static final double TAMING_SPELL_MOVE_TIMEOUT = 0.025f;

    private Direction flightDirection;
    private Runnable tameCallback;
    private Actor tamer;
    
    public TamingSpellOnTickBehavior(Map<String, Object> instantiationParams){

        flightDirection = Util.toDirection((Vec2i)instantiationParams.get("ShootDirection"));
        tameCallback = (Runnable)instantiationParams.get("TameCallback");
        tamer = (Actor)instantiationParams.get("Tamer");
    }
    
    @Override
    public void invoke(Actor entity, double timeDelta) {       
        for(Message m : entity.getMessageBus()){
            switch(m.getType()){
                case EntityMoveFailed:
                {
                    Actor a = (Actor)m.getMsgObject();
                    if(a == entity){
                        entity.terminate();
                        return;
                    }
                    break;
                }
            }
        }
        
        char arrow_represent_override;
        switch(flightDirection){
            case LEFT:
                arrow_represent_override = '\u25C4';
                break;
            case RIGHT:
                arrow_represent_override = '\u25BA';
                break;
            case UP:
                arrow_represent_override = '\u25B2';
                break;
            case DOWN:
                arrow_represent_override = '\u25BC';
                break;
            default:
                throw new RuntimeException("No flightDirection specified!");
        }
        
        entity.setRepresentOverride(new TerminalChar(arrow_represent_override, entity.getRepresent().getFGColor(), entity.getRepresent().getBGColor()));
    
        /**************
         * Don't even attempt taming if we're terminated
         * which probably means that a previous taming
         * attempt was made (or we hit a wall)
         */
        if(entity.isTerminated())
            return;
        
        /**********************
         * See if a tameable actor is around
         */
        List<Actor> tameable_actors = new LinkedList<Actor>();
        tameable_actors.addAll(entity.getView().getActorsAt(entity.getPos()).stream().filter(a -> a.isTameable()).collect(Collectors.toList()));
        
        /**********************
         * Tame the first tameable if it exists
         */
        if(tameable_actors.size() > 0){
            AttemptTameParams atp = new AttemptTameParams();
            atp.tamerActor = tamer;
            atp.tamedActor = tameable_actors.get(0);
            atp.tameCallback = (Boolean wasTamed) -> {
                tameCallback.run();
            };        
            entity.sendBusMessage(new Message(AttemptTame, atp));
            entity.terminate();
        }
        
        if(!entity.canMove())
            return;

        EntityMoveParams emp = new EntityMoveParams();
        emp.direction = flightDirection;
        emp.entity = entity;

        entity.sendBusMessage(new Message(Message.Type.EntityMove, emp));

        entity.setMoveTimeout(TAMING_SPELL_MOVE_TIMEOUT);
    }
    
}
