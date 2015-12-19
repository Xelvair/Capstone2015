package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class NotificationPanel{
    public static Panel render(String message, int width, int height){
        Panel p = new Panel(width, height);
        p.insert(Panel.fillPanel(width, height, new TerminalChar(' ', Color.BLACK, Color.WHITE)), 0, 0);
        p.insertCenter(Panel.textPanel(message, Color.WHITE, Color.DARK_GRAY));
        return p;
    }
}
