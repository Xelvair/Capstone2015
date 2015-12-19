package capstone2015.messaging;

import java.util.Iterator;
import java.util.LinkedList;

public class MessageBus implements Iterable<Message>{
    private LinkedList<Message> curMessages = new LinkedList<>();
    private LinkedList<Message> bufMessages = new LinkedList<>();
    
    public void enqueue(Message msg){
        bufMessages.add(msg);
    }
    
    public void refresh(){
        curMessages = bufMessages;
        bufMessages = new LinkedList<>();
    }
    
    public int size(){
        return curMessages.size();
    }
    
    public Message get(int idx){
        return curMessages.get(idx);
    }

    @Override
    public Iterator<Message> iterator() {
        return curMessages.iterator();
    }
}
