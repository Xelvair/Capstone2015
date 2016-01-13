package capstone2015.entity;

import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;

public abstract class EntityBase {
    /*package private*/ EntityProto proto;
    /*package private*/ MessageBus messageBus;
    /*package private*/ EntityBase parent;

    public final EntityBase getParent(){
        return parent;
    }
    
    public final EntityProto getProto(){
        return proto;
    }
    
    public final void sendBusMessage(Message msg){
        messageBus.enqueue(msg);
    }
    
    public final MessageBus getMessageBus(){
        return messageBus;
    }
    
    public abstract TerminalChar getRepresent();
    public abstract String getName();
    public abstract String getDescription();
}
