package capstone2015.game.panel;

import capstone2015.entity.Actor;
import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;
import java.util.List;

public class FollowerPanel {
    
    public static final int BORDER_WIDTH = 1;
    public static final int FOLLOWER_ENTRY_HEIGHT = 2;
    public static final int HORIZONTAL_PADDING = 1;
    
    public static Panel render(List<Actor> followers){
        int longest_follower_name = -1;
        for(Actor a : followers){
            longest_follower_name = Math.max(longest_follower_name, a.getName().length());
        }
        
        int panel_inner_width = longest_follower_name + HORIZONTAL_PADDING * 2 + 2;
        int panel_width = panel_inner_width + BORDER_WIDTH * 2;
        int panel_height = followers.size() * FOLLOWER_ENTRY_HEIGHT + BORDER_WIDTH * 2;
        Panel p = Panel.borderPanel(panel_width, panel_height, Color.DARK_GRAY, Color.BLACK);
        
        int idx = 0;
        for(Actor a : followers){
            Panel p_follower = Panel.fillPanel(panel_inner_width, FOLLOWER_ENTRY_HEIGHT, new TerminalChar(' ', Color.WHITE, Color.BLACK));
            p_follower.set(new Vec2i(0, 0), new TerminalChar(a.getRepresent().getCharacter(), a.getRepresent().getFGColor(), Color.BLACK));
            p_follower.insert(Panel.textPanel(a.getName(), Color.WHITE, Color.BLACK), 2, 0);
            
            int max_hp = a.getMaxHealth();
            int hp = a.getHealth();
            double health_coef = (double)hp / (double)max_hp;
            
            Color bar_color = Color.RED;
            
            p_follower.insert(Panel.fillPanel((int)((double)panel_inner_width * health_coef), 1, new TerminalChar(' ', Color.WHITE, bar_color)), 0, 1);
            
            p.insert(p_follower, 1, 1 + FOLLOWER_ENTRY_HEIGHT * idx++);
        }
        
        p.insertCenterHorizontally(Panel.textPanel("Followers", Color.WHITE, Color.DARK_GRAY), 0);
        
        return p;
    }
}
