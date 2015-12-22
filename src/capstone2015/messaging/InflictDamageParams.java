package capstone2015.messaging;

import capstone2015.entity.Actor;
import capstone2015.geom.Vec2i;

public class InflictDamageParams {
    private Actor damagingEntity;
    private Vec2i position;
    private int damage;

    public InflictDamageParams(Actor damagingEntity, Vec2i position, int damage){
        this.damagingEntity = damagingEntity;
        this.position = position;
        this.damage = damage;
    }
    
    public Actor getDamagingEntity() {
        return damagingEntity;
    }

    public Vec2i getPosition() {
        return position;
    }
    
    public int getDamage(){
        return damage;
    }
}
