package capstone2015.messaging;

public class Message {    
    private int type;
    private Object msgObject;
    
    public Message(int type, Object msgObject){
        this.type = type;
        this.msgObject = msgObject;
    }
    public Message(int type){
        this(type, null);
    }
    
    public int getType(){
        return this.type;
    }
    
    public Object getMsgObject(){
        return this.msgObject;
    }
}
