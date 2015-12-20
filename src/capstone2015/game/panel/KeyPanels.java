package capstone2015.game.panel;

import capstone2015.game.EntityProto;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

/***********
 * Key Panels are the panels describing the icons the player
 * encounters ingame. Each panel shall have a width of 60 while
 * the height stays variable, but can only go up to 20
 */

public class KeyPanels {
    public static Panel render(int entityId){
        TerminalChar bg_char = new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY);
        TerminalChar entity_represent = EntityProto.get(entityId).getRepresentVisible();
        String entity_name = EntityProto.get(entityId).getName();
        String entity_desc = EntityProto.get(entityId).getDescription();
        int header_length = entity_name.length() + 2;
        
        Panel p = Panel.fillPanel(60, 20, bg_char);
        
        Panel p_header = Panel.fillPanel(header_length, 1, bg_char);
        p_header.set(0, 0, entity_represent);
        
        p_header.insert(Panel.textPanel(entity_name, Color.WHITE, Color.DARK_GRAY), 2, 0);
        
        p.insert(Panel.textPanel(entity_desc, Color.WHITE, Color.DARK_GRAY), 1, 3);
        
        p.insertCenterHorizontally(p_header, 1);
        
        return p;
    }
}
