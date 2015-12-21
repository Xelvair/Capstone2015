package capstone2015.game.panel;

import capstone2015.game.ActiveEntity;
import capstone2015.game.EntityProto;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class HudPanel {
    public static Panel render(ActiveEntity e, int width){
        Panel p = Panel.fillPanel(width, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        
        String health_str = "";
        
        int entity_max_hp = EntityProto.get(e.getId()).getHealthPoints();
        int entity_hp = e.getHealthPoints();
        
        for(int i = 0; i < e.getHealthPoints(); i++){
            health_str += '\u2665';
        }
        
        Panel p_health = Panel.fillPanel(entity_max_hp, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        p_health.insert(Panel.textPanel(health_str, Color.RED, Color.DARK_GRAY), 0, 0);
        
        p.insert(p_health, p.width() - p_health.width(), 0);
        return p;
    }
}
