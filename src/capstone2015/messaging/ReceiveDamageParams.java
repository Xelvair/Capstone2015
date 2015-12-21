package capstone2015.messaging;

import capstone2015.game.ActiveEntity;

public class ReceiveDamageParams {
    private ActiveEntity damagedEntity;
    private ActiveEntity damagingEntity;
    private int damage;

    public ReceiveDamageParams(
            ActiveEntity damagedEntity, 
            ActiveEntity damagingEntity, 
            int damage
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
