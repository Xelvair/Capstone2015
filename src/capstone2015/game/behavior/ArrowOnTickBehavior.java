package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.*;
import capstone2015.util.Util;
import java.util.Map;

public class ArrowOnTickBehavior implements OnTickBehavior{

    public static int ARROW_DAMAGE = 3;
    public static double ARROW_MOVE_TIMEOUT = 0.03f;

    private boolean isAirborne = false;
    private Direction flightDirection;
    private int teamId;

    public ArrowOnTickBehavior(Actor actor, Map<String, Object> instantiationParams){
        if(
               instantiationParams.containsKey("ShootDirection")
            && instantiationParams.containsKey("TeamId")
        ){
            isAirborne = true;
            flightDirection = Util.toDirection((Vec2i)instantiationParams.get("ShootDirection"));
            teamId = (int)instantiationParams.get("TeamId");
        }
    }

    @Override
    public void invoke(Actor entity, double timeDelta) {

        /*********************************************
         * NOP if we're not airborne
         */
        if(!isAirborne)
            return;

        /*********************************************
         * If the arrow collided with a wall, stop the flying and return to
         * normal state
         */
        Runnable exit_airborne = () -> {
            isAirborne = false;
            flightDirection = Direction.NONE;
            teamId = -1;
        };

        for(Message m : entity.getMessageBus()){
            switch(m.getType()){
                case GameMessage.ENTITY_MOVE_FAILED:
                {
                    Actor a = (Actor)m.getMsgObject();
                    if(a == entity){
                        exit_airborne.run();
                        return;
                    }
                    break;
                }
                case GameMessage.RECEIVED_DAMAGE:
                {
                    ReceivedDamageParams rdp = (ReceivedDamageParams)m.getMsgObject();
                    if(rdp.damagingActor == entity){
                        exit_airborne.run();
                        return;
                    }
                    break;
                }
            }
        }

        char arrow_represent_override;
        switch(flightDirection){
            case LEFT:
                arrow_represent_override = '\u2190';
                break;
            case RIGHT:
                arrow_represent_override = '\u2192';
                break;
            case UP:
                arrow_represent_override = '\u2191';
                break;
            case DOWN:
            default:
                arrow_represent_override = '\u2193';
                break;
        }
        
        entity.setRepresentOverride(new TerminalChar(arrow_represent_override, entity.getRepresent().getFGColor(), entity.getRepresent().getBGColor()));

        InflictDamageParams idp = new InflictDamageParams();
        idp.damage = ARROW_DAMAGE;
        idp.position = entity.getPos();
        idp.damagingEntity = entity;
        idp.teamId = teamId;

        entity.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp));

        if(!entity.canMove())
            return;

        EntityMoveParams emp = new EntityMoveParams();
        emp.direction = flightDirection;
        emp.entity = entity;

        entity.sendBusMessage(new Message(GameMessage.ENTITY_MOVE, emp));

        entity.setMoveTimeout(ARROW_MOVE_TIMEOUT);
    }
}
