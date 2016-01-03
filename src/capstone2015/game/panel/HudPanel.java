package capstone2015.game.panel;

import capstone2015.entity.Actor;
import capstone2015.game.Inventory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class HudPanel {
    public static Panel render(int entity_hp, int entity_max_hp, Inventory inventory, int width){
        Panel p = Panel.fillPanel(width, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        
        String health_str = "";
        
        for(int i = 0; i < entity_hp; i++){
            health_str += '\u2665';
        }
        
        Panel p_health = Panel.fillPanel(entity_max_hp, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        p_health.insert(Panel.textPanel(health_str, Color.RED, Color.DARK_GRAY), 0, 0);
        
        p.insert(p_health, p.width() - p_health.width(), 0);
        
        if(inventory != null){
            p.insert(InventoryPanel.render(inventory, Color.GRAY, Color.DARK_GRAY), 3, 0);
        }
        return p;
    }
}
