package capstone2015.game;

import capstone2015.util.Pair;
import java.awt.Color;
import java.util.LinkedList;

public class NotificationList {
    private LinkedList<Pair<String, Color>> notifications = new LinkedList<>();
    private int capacity;
    
    public NotificationList(int capacity){
        this.capacity = capacity;
    }
    
    public void push(String text, Color color){
        notifications.add(new Pair<>(text, color));
        if(notifications.size() > capacity){
            notifications.removeFirst();
        }
    }
    
    public LinkedList<Pair<String, Color>> getNotifications(){
        return this.notifications;
    }
    
    public int getCapacity(){
        return capacity;
    }
}
