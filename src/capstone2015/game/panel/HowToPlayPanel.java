package capstone2015.game.panel;

import capstone2015.entity.EntityFactory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;

import java.awt.*;

public class HowToPlayPanel {
    public static Panel render(){
        String how_to_play = ""
            + "Your goal is to reach and open the exit( ) using a key( )\n"
            + "found within the dungeon.\n\n"
            + "You will have to avoid foes such as Snakes( ) and\n"
            + "bonfires( ) along the way.\n"
            + "Be careful of those, for if you take too much\n"
            + "damage, you will die, and the game will be lost.\n\n"
            + "Within the dungeon, there are items that can help\n"
            + "you on your journey, such as weapons, potions\n"
            + "and other usables. Refer to the Legend\n"
            + "to find out more about these items.\n\n"
            + "When you reach the exit( ), use the key( ) on it\n"
            + "to open the door and win the game.";

        Panel content = Panel.textPanel(how_to_play, Color.WHITE, Color.DARK_GRAY);

        content.set(40, 0, EntityFactory.getProto(EntityFactory.ID_EXIT).entityBaseProto.represent);
        content.set(55, 0, EntityFactory.getProto(EntityFactory.ID_KEY).entityBaseProto.represent);
        content.set(43, 3, EntityFactory.getProto(EntityFactory.ID_RATTLESNAKE).entityBaseProto.represent);
        content.set(9, 4, EntityFactory.getProto(EntityFactory.ID_BONFIRE).entityBaseProto.represent);
        content.set(24, 13, EntityFactory.getProto(EntityFactory.ID_EXIT).entityBaseProto.represent);
        content.set(40, 13, EntityFactory.getProto(EntityFactory.ID_KEY).entityBaseProto.represent);

        Panel p = Panel.fillPanel(60, content.height() + 2, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));

        p.insert(content, 1, 1);

        return p;
    }
}
