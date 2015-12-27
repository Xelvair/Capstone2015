package capstone2015.messaging;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;

public class ReceivedDamageParams {
    private Actor damagedEntity;
    private EntityBase damagingEntity;
    private int damage;

    public ReceivedDamageParams(
            Actor damagedEntity, 
            EntityBase damagingEntity, 
            int damage
    ){
        this.damagedEntity = damagedEntity;
        this.damagingEntity = damagingEntity;
        this.damage = damage;
    }
    
    public int getDamage() {
        return damage;
    }

    public EntityBase getDamagingEntity() {
        return damagingEntity;
    }
    
    public Actor getDamagedEntity() {
        return damagedEntity;
    }
   
}
