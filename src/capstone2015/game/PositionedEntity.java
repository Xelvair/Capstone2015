package capstone2015.game;

public class PositionedEntity extends Entity{
    
  private int xPos;
  private int yPos;
  
  public PositionedEntity(int entityId, int xPos, int yPos){
      super(entityId);
      this.xPos = xPos;
      this.yPos = yPos;
  }
  
  public int getXPos(){return xPos;}
  public int getYPos(){return yPos;}
  
  public void setXPos(int xPos){this.xPos = xPos;}
  public void setYPos(int yPos){this.yPos = yPos;}
    
}
