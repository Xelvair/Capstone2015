package capstone2015.game.panel;

import capstone2015.game.NotificationList;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Pair;
import java.awt.Color;
import java.util.LinkedList;

public class NotificationPanel{
    public static Panel render(NotificationList notificationList, int width){
        Panel p = new Panel(width, notificationList.getCapacity());
        p.insert(Panel.fillPanel(width, p.height(), new TerminalChar(' ', Color.BLACK, Color.DARK_GRAY)), 0, 0);
        
        LinkedList<Pair<String, Color>> notifications = notificationList.getNotifications();
        int y = 0;
        for(Pair<String, Color> notification_entry : notifications){
            String notif_text = notification_entry.getFirst();
            Color notif_color = notification_entry.getSecond();
            Panel p_notif_entry = Panel.textPanel(notif_text, notif_color, Color.DARK_GRAY);
            
            p.insertCenterHorizontally(p_notif_entry, y);
            ++y;
        }
        return p;
    }
}
