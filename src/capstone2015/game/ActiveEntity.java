package capstone2015.game;

import capstone2015.game.behavior.OnDamageBehavior;
import capstone2015.game.behavior.OnTickBehavior;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.messaging.OnDamageParams;
import java.util.HashMap;

public class ActiveEntity extends Entity{
    
  private int               xPos;
  private int               yPos;
  private int               healthPoints;
  private OnTickBehavior    onTickBehavior;
  private OnDamageBehavior  onDamageBehavior;
  private final HashMap<ActiveEntity, Double> damageIgnoreTimers = new HashMap<>();
  
  public ActiveEntity(int entityId, int xPos, int yPos, MessageBus messageBus){
      super(entityId, messageBus);
      this.xPos = xPos;
      this.yPos = yPos;
      this.healthPoints = this.proto.getHealthPoints();
      this.onTickBehavior = proto.createOnTickBehavior();
      this.onDamageBehavior = proto.createOnDamageBehavior();
      
  }
  
  public int getXPos(){return xPos;}
  public int getYPos(){return yPos;}
  
  @Override
  public int getHealthPoints(){return healthPoints;}
  
  @Override
  public OnTickBehavior getOnTickBehavior(){
      return onTickBehavior;
  }
  
  @Override
  public OnDamageBehavior getOnDamageBehavior(){
      return onDamageBehavior;
  }
  
  @Override
  public HashMap<ActiveEntity, Double> getDamageIgnoreTimers(){
      return this.damageIgnoreTimers;
  }
  
  public void setXPos(int xPos){this.xPos = xPos;}
  public void setYPos(int yPos){this.yPos = yPos;}
  public void setHealthPoints(int healthPoints){
      this.healthPoints = healthPoints;
  }
    
}
