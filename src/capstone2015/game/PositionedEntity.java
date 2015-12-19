package capstone2015.game;

import capstone2015.messaging.MessageBus;

public class PositionedEntity extends Entity{
    
  private int xPos;
  private int yPos;
  
  public PositionedEntity(int entityId, int xPos, int yPos, MessageBus messageBus){
      super(entityId, messageBus);
      this.xPos = xPos;
      this.yPos = yPos;
  }
  
  public int getXPos(){return xPos;}
  public int getYPos(){return yPos;}
  
  public void setXPos(int xPos){this.xPos = xPos;}
  public void setYPos(int yPos){this.yPos = yPos;}
    
}
