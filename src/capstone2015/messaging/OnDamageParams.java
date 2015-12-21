package capstone2015.messaging;

import capstone2015.game.ActiveEntity;

public class OnDamageParams {
    private ActiveEntity damagedEntity;
    private ActiveEntity damagingEntity;
    private int damage;

    public OnDamageParams(
            ActiveEntity damagedEntity, 
            ActiveEntity damagingEntity, 
            int damageDealt
    ){
        this.damagedEntity = damagedEntity;
        this.damagingEntity = damagingEntity;
        this.damage = damage;
    }
    
    public int getDamage() {
        return damage;
    }

    public ActiveEntity getDamagingEntity() {
        return damagingEntity;
    }
    
    public ActiveEntity getDamagedEntity() {
        return damagedEntity;
    }
   
}
