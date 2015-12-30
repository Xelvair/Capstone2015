package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.game.Direction;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.EntityMoveParams;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import capstone2015.util.Util;

import java.awt.*;
import java.util.Map;

public class ArrowOnTickBehavior implements OnTickBehavior{

    public static int BOW_DAMAGE = 3;
    public static double BOW_MOVE_TIMEOUT = 0.03f;

    private boolean isAirborne = false;
    private Direction flightDirection;
    private int teamId;

    public ArrowOnTickBehavior(Map<String, Object> instantiationParams){
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
        if(!isAirborne)
            return;

        if(flightDirection == Direction.LEFT || flightDirection == Direction.RIGHT){
            entity.setRepresentOverride(new TerminalChar('-', entity.getRepresent().getFGColor(), entity.getRepresent().getBGColor()));
        } else {
            entity.setRepresentOverride(new TerminalChar('|', entity.getRepresent().getFGColor(), entity.getRepresent().getBGColor()));
        }

        InflictDamageParams idp = new InflictDamageParams();
        idp.damage = BOW_DAMAGE;
        idp.position = entity.getPos();
        idp.damagingEntity = entity;
        idp.teamId = teamId;

        entity.sendBusMessage(new Message(Message.Type.InflictDamage, idp));

        if(!entity.canMove())
            return;

        EntityMoveParams emp = new EntityMoveParams();
        emp.direction = flightDirection;
        emp.entity = entity;

        entity.sendBusMessage(new Message(Message.Type.EntityMove, emp));

        entity.setMoveTimeout(BOW_MOVE_TIMEOUT);
    }
}
