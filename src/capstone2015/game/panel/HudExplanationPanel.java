package capstone2015.game.panel;

import capstone2015.entity.EntityFactory;
import capstone2015.game.Inventory;
import capstone2015.game.NotificationList;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;

import java.awt.*;
import java.util.LinkedList;

public class HudExplanationPanel {
    public static Panel render(){
        /*************************
         * Draw the heading
         */
        Panel p_heading = Panel.fillPanel(60, 3, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        p_heading.insert(Panel.textPanel("This is an example of the ingame HUD:", Color.WHITE, Color.DARK_GRAY), 1, 1);

        /*************************
         * Draw an example HUD with a notification bar and
         * a status bar (inventory, health)
         */
        Panel p_hud = Panel.fillPanel(60, 10, new TerminalChar(' ', Color.WHITE, Color.BLACK));

        NotificationList nfl = new NotificationList(1);
        nfl.push("This is a notification!", Color.YELLOW);
        p_hud.insertCenterHorizontally(NotificationPanel.render(nfl, p_hud.width()), 0);

        Inventory inv = new Inventory(5);
        inv.add(EntityFactory.createItem(EntityFactory.ID_SWORD));
        inv.add(EntityFactory.createItem(EntityFactory.ID_HEALTH_POTION));
        inv.add(EntityFactory.createItem(EntityFactory.ID_KEY));
        p_hud.insertCenterHorizontally(HudPanel.render(7, 10, inv, p_hud.width()), p_hud.height() - 1);

        p_hud.set(30, 1, new TerminalChar('1', Color.GREEN, Color.BLACK));
        p_hud.set(7, 8, new TerminalChar('2', Color.GREEN, Color.BLACK));
        p_hud.set(53, 8, new TerminalChar('3', Color.GREEN, Color.BLACK));

        /*************************
         * Content of the page
         */
        String hud_explanation = ""
                + " : The notification bar. It displays information about\n"
                + "   things that are currently happening in the game.\n\n"
                + " : Your inventory. All items that you currently own\n"
                + "   are displayed here. Select an item with the number-\n"
                + "   keys. Selected items will be highlighted.\n"
                + "   To use an item, select it, and press either F for a\n"
                + "   non-directional use, or one of the WASD-Keys to use\n"
                + "   the item in a particular direction.\n\n"
                + " : Your current health. Each heart represents one\n"
                + "   Health-Point. If you run out of Health-Points,\n"
                + "   you are dead, and the game ends.\n";
        Panel p_hud_explanation = Panel.textPanel(hud_explanation, Color.WHITE, Color.DARK_GRAY);

        p_hud_explanation.set(0, 0, new TerminalChar('1', Color.GREEN, Color.DARK_GRAY));
        p_hud_explanation.set(0, 3, new TerminalChar('2', Color.GREEN, Color.DARK_GRAY));
        p_hud_explanation.set(0, 10, new TerminalChar('3', Color.GREEN, Color.DARK_GRAY));

        Panel p_content = Panel.fillPanel(60, p_hud_explanation.height() + 2, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        p_content.insert(p_hud_explanation, 1, 1);

        LinkedList<Panel> panels = new LinkedList<>();
        panels.add(p_heading);
        panels.add(p_hud);
        panels.add(p_content);

        return Panel.concatVertically(panels);
    }
}
