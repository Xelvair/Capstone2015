package capstone2015.messaging;

public class Message {
    public enum Type{
        KeyEvent,
        GameEvent,
        PushIngameMenuState,
        QuitToDesktop;
    }
    
    private Type type;
    private Object msgObject;
    
    public Message(Type type, Object msgObject){
        this.type = type;
        this.msgObject = msgObject;
    }
    public Message(Type type){
        this(type, null);
    }
    
    public Type getType(){
        return this.type;
    }
    
    public Object getMsgObject(){
        return this.msgObject;
    }
}
