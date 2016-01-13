package capstone2015.game.panel;

import capstone2015.game.Inventory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class HudPanel {
    public static final int MAX_SHOWN_HP = 20;
    
    public static Panel render(int entity_hp, int entity_max_hp, Inventory inventory, int width){
        
        Panel p = Panel.fillPanel(width, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        
        String health_str = "";
        
        int shown_entity_hp = Math.min(MAX_SHOWN_HP, entity_hp);
        
        for(int i = 0; i < shown_entity_hp; i++){
            if(i == 0 && shown_entity_hp < entity_hp)
                health_str += "+";
            else
                health_str += '\u2665';
        }
        
        Panel p_health = Panel.fillPanel(shown_entity_hp, 1, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        p_health.insert(Panel.textPanel(health_str, Color.RED, Color.DARK_GRAY), 0, 0);
        
        p.insert(p_health, p.width() - p_health.width(), 0);
        
        if(inventory != null){
            p.insert(InventoryPanel.render(inventory, Color.GRAY, Color.DARK_GRAY), 3, 0);
        }

        return p;
    }
}
