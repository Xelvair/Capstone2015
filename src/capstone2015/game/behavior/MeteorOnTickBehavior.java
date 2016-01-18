package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import static capstone2015.entity.EntityFactory.SHADER_BONFIRE;
import capstone2015.game.GameMessage;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.InflictDamageParams;
import capstone2015.messaging.Message;
import java.awt.Color;

public class MeteorOnTickBehavior implements OnTickBehavior{
    public static final Color   METEOR_FALL_COLOR = new Color(0, 0, 0);
    public static final Color   METEOR_BIGDAMAGE_COLOR = new Color(255, 130, 0);
    public static final Color   METEOR_BURN_COLOR = new Color(255, 0, 0);
    
    public static final int     BIGDAMAGE = 3;
    public static final int     DAMAGE = 1;
    
    public static final double FALL_STAGE1_END = 1.d;
    public static final double FALL_STAGE2_END = 2.d;
    public static final double FALL_STAGE3_END = 3.d;
    public static final double BIG_DAMAGE_REPRESENT_END = 3.5d;
    public static final double MAX_LIFETIME = 10.d;
    
    double lifetime = 0.d;
    boolean hasDoneBigDamage = false;
    
    @Override
    public void invoke(Actor actor, double timeDelta) {
        if(lifetime < FALL_STAGE1_END){
            actor.setRepresentOverride(new TerminalChar('\u2591', METEOR_FALL_COLOR, Color.WHITE));
        } else if(lifetime < FALL_STAGE2_END){
            actor.setRepresentOverride(new TerminalChar('\u2592', METEOR_FALL_COLOR, Color.WHITE));
        } else if(lifetime < FALL_STAGE3_END){
            actor.setRepresentOverride(new TerminalChar('\u2593', METEOR_FALL_COLOR, Color.WHITE));
        } else if(lifetime < BIG_DAMAGE_REPRESENT_END){
            if(!hasDoneBigDamage){
                InflictDamageParams idp = new InflictDamageParams();
                idp.damage = BIGDAMAGE;
                idp.damagingEntity = actor;
                idp.position = actor.getPos();
                idp.teamId = actor.getTeamId();
                
                actor.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp));
                hasDoneBigDamage = true;
            }
            actor.setRepresentOverride(new TerminalChar('X', METEOR_BIGDAMAGE_COLOR, Color.WHITE));
            actor.setShaderTypeOverride(SHADER_BONFIRE);
        } else if(lifetime < MAX_LIFETIME){
            actor.setRepresentOverride(new TerminalChar('^', METEOR_BURN_COLOR, Color.WHITE));
            actor.setShaderTypeOverride(SHADER_BONFIRE);
            
            InflictDamageParams idp = new InflictDamageParams();
            idp.damage = DAMAGE;
            idp.damagingEntity = actor;
            idp.position = actor.getPos();
            idp.teamId = actor.getTeamId();

            actor.sendBusMessage(new Message(GameMessage.INFLICT_DAMAGE, idp));
        } else {
            actor.terminate();
        }
        
        lifetime += timeDelta;
    }
}
