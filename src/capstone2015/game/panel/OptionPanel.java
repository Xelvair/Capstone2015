package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Util;
import com.googlecode.lanterna.terminal.Terminal;
import java.util.Arrays;
import java.awt.Color;

public class OptionPanel {
    public static class Config{
        public int marginV = 1;
        public int marginH = 3;
        public int borderWidth = 1;
        public char selectorIcon = '>';
        public int optionPadding = 1;
        public Color bgColor = Color.DARK_GRAY;
        public Color borderColor = Color.WHITE;
        public Color selectorColor = Color.WHITE;
        public Color optionColor = Color.WHITE;
    }    
    
    public static Panel render(String[] options, Config config, int selection){
        int panel_width = Util.maxLength(Arrays.asList(options)) 
                        + (config.marginH * 2) 
                        + 2 
                        + (config.borderWidth * 2);
        
        int panel_height = options.length 
                         + (config.optionPadding * (options.length - 1)) 
                         + (config.marginV * 2) + (config.borderWidth * 2);
        
        Panel option_panel = Panel.fillPanel(panel_width, panel_height, new TerminalChar(' ', Color.WHITE, config.borderColor));
        option_panel.insertCenter(Panel.fillPanel(panel_width - 2, panel_height - 2, new TerminalChar(' ', Color.WHITE, config.bgColor)));
     
        for(int i = 0; i < options.length; i++){
            String option = new String(options[i]);
            option = (i == selection ? "> " : "  ") + option;
            option_panel.insert(
                    Panel.textPanel(
                            option, 
                            config.optionColor, 
                            config.bgColor), 
                    config.borderWidth + config.marginH, 
                    config.borderWidth + config.marginV + i * (config.optionPadding + 1)
            );
        }
        
        return option_panel;
    }
}
