package capstone2015.game;

public class EntityAction {
    private EntityActionType type;
    private Object obj;
    
    public EntityAction(EntityActionType type, Object obj){
        this.type = type;
    }
    public EntityAction(EntityActionType type){
        this(type, null);
    }
    
    public EntityActionType getType(){return this.type;}
    public Object getObject(){return this.obj;}
    public void setType(EntityActionType type){this.type = type;}
    public void setObject(Object obj){this.obj = obj;}
}
