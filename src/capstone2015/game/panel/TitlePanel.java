package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import java.awt.Color;

public class TitlePanel {
    public static Panel render(){
        String title = "";
        title += " _______  _______  _______  _______ _________ _______  _        _______ \n";
        title += "(  ____ \\(  ___  )(  ____ )(  ____ \\\\__   __/(  ___  )( (    /|(  ____ \\\n";
        title += "| (    \\/| (   ) || (    )|| (    \\/   ) (   | (   ) ||  \\  ( || (    \\/\n";
        title += "| |      | (___) || (____)|| (_____    | |   | |   | ||   \\ | || (__    \n";
        title += "| |      |  ___  ||  _____)(_____  )   | |   | |   | || (\\ \\) ||  __)   \n";
        title += "| |      | (   ) || (            ) |   | |   | |   | || | \\   || (      \n";
        title += "| (____/\\| )   ( || )      /\\____) |   | |   | (___) || )  \\  || (____/\\\n";
        title += "(_______/|/     \\||/       \\_______)   )_(   (_______)|/    )_)(_______/\n";
                                                                        
        
        return Panel.textPanel(title, Color.WHITE, Color.BLACK);
    }
}
