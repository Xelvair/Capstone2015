package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class ControlsPanel {
    public static Panel render(){
        Panel p_text = Panel.textPanel(
                "\u25B2\u25BA\u25BC\u25C4:             Move in a direction.\n" +
                "CTRL + \u25B2\u25BA\u25BC\u25C4:      QuickMove in a direction.\n" + 
                "F:                Use an item on yourself.\n" +
                "WASD:             Use an item in a direction.\n" + 
                "F1:               Show diagnostics window."
        , Color.WHITE, Color.DARK_GRAY);
        
        Panel p = Panel.fillPanel(60, p_text.height() + 3, new TerminalChar(' ', Color.WHITE, Color.DARK_GRAY));
        
        p.insert(p_text, 1, 3);
        
        return p;
    }
}
