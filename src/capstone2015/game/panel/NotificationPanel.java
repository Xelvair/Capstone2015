package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class NotificationPanel extends Panel{
    public NotificationPanel(String message, int width, int height){
        super(width, height);
        this.insert(Panel.fillPanel(width, height, new TerminalChar(' ', Color.BLACK, Color.WHITE)), 0, 0);
        this.insertCenter(Panel.textPanel(message, Color.WHITE, Color.DARK_GRAY));
    }
}
