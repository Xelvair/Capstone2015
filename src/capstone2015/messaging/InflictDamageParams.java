package capstone2015.messaging;

import capstone2015.game.ActiveEntity;
import capstone2015.geom.Vec2i;

public class InflictDamageParams {
    private ActiveEntity damagingEntity;
    private Vec2i position;
    private int damage;

    public InflictDamageParams(ActiveEntity damagingEntity, Vec2i position, int damage){
        this.damagingEntity = damagingEntity;
        this.position = position;
        this.damage = damage;
    }
    
    public ActiveEntity getDamagingEntity() {
        return damagingEntity;
    }

    public Vec2i getPosition() {
        return position;
    }
    
    public int getDamage(){
        return damage;
    }
}
