package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Util;
import com.googlecode.lanterna.terminal.Terminal;
import java.util.Arrays;
import java.awt.Color;

public class OptionPanel {
    public static final int MARGIN_V = 1;
    public static final int MARGIN_H = 3;
    public static final int BORDER_WIDTH = 1;
    public static final char SELECTOR_ICON = '>';
    public static final int OPTION_PADDING = 1;
    public static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    public static final Color BORDER_COLOR = Color.WHITE;
    public static final Color SELECTOR_COLOR = Color.WHITE;
    public static final Color OPTION_COLOR = Color.WHITE;
    
    
    public static Panel render(String[] options, int selection){
        int panel_width = Util.maxLength(Arrays.asList(options)) + (MARGIN_H * 2) + 2 + (BORDER_WIDTH * 2);
        int panel_height = options.length + (OPTION_PADDING * (options.length - 1)) + (MARGIN_V * 2) + (BORDER_WIDTH * 2);
        
        Panel option_panel = Panel.fillPanel(panel_width, panel_height, new TerminalChar(' ', Color.WHITE, BORDER_COLOR));
        option_panel.insertCenter(Panel.fillPanel(panel_width - 2, panel_height - 2, new TerminalChar(' ', Color.WHITE, BACKGROUND_COLOR)));
     
        for(int i = 0; i < options.length; i++){
            String option = new String(options[i]);
            option = (i == selection ? "> " : "  ") + option;
            option_panel.insert(Panel.textPanel(option, OPTION_COLOR, BACKGROUND_COLOR), BORDER_WIDTH + MARGIN_H, BORDER_WIDTH + MARGIN_V + i * (OPTION_PADDING + 1));
        }
        
        return option_panel;
    }
}
