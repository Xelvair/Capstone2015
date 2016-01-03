package capstone2015.game.panel;

import capstone2015.entity.EntityFactory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

/***********
 * Key Panels are the panels describing the icons the player
 * encounters ingame. Each panel shall have a width of 60 while
 * the height stays variable
 */

public class KeyPanels {
    public static Panel render(int entityId){
        TerminalChar bg_char = new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY);
        TerminalChar entity_represent = EntityFactory.getProto(entityId).entityBaseProto.represent;
        String entity_name = EntityFactory.getProto(entityId).entityBaseProto.name;
        String entity_desc = EntityFactory.getProto(entityId).entityBaseProto.description;
        int header_length = entity_name.length() + 2;

        /********************
         * Header panel
         */
        Panel p_header = Panel.fillPanel(header_length, 1, bg_char);
        p_header.set(0, 0, entity_represent);
        p_header.insert(Panel.textPanel(entity_name, Color.WHITE, Color.DARK_GRAY), 2, 0);

        /********************
         * Content panel
         */
        Panel p_content = Panel.textPanel(entity_desc, Color.WHITE, Color.DARK_GRAY);

        /********************
         * Putting it together
         */
        Panel p = Panel.fillPanel(60, p_header.height() + p_content.height() + 3, bg_char);
        p.insertCenterHorizontally(p_header, 1);
        p.insert(p_content, 1, 3);

        return p;
    }
}
