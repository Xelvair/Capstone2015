package capstone2015.game;

class PositionedEntity extends Entity{
    
  private int xPos;
  private int yPos;
  
  public PositionedEntity(int entityId, int xPos, int yPos){
      super(entityId);
      this.xPos = xPos;
      this.yPos = yPos;
  }
    
}
