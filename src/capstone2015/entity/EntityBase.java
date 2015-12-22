package capstone2015.entity;

import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;

public abstract class EntityBase {
    /*package private*/ EntityProto entityProto;
    /*package private*/ MessageBus messageBus;
    
    public final EntityProto getProto(){
        return entityProto;
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
