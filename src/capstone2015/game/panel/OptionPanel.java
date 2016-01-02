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
        public int optionPadding = 1;
        public Color bgColor = Color.DARK_GRAY;
        public Color borderColor = Color.WHITE;
        public Color optionColor = Color.WHITE;
        public Color headingColor = Color.BLACK;
        public String heading = "";
        public int maxOptions = 7;
    }    

    private Config config;
    private int selection;
    private int list_offset = 0;
    private String[] options;
    private int shown_options = 0;

    public OptionPanel(String[] options, Config config, int selection){
        this.options = options;
        this.config = config;
        this.selection = selection;
        this.shown_options = Math.min(options.length, config.maxOptions);

    }

    public int getSelection(){
        return selection;
    }

    public void nextSelection() {
        selection = (selection + 1) % options.length;
        align();
    }

    public void prevSelection(){
        selection = (selection + (options.length - 1)) % options.length;
        align();
    }

    private void align(){
        if(selection > list_offset + shown_options - 1){
            this.list_offset = selection - (shown_options - 1);
            return;
        }

        if(selection < list_offset){
            list_offset = selection;
            return;
        }
    }

    public Panel render(){
        /**********************************
         * Determine panel size and create panel
         */
        int panel_width = Math.max(
                Util.maxLength(Arrays.asList(options)) + (config.marginH * 2) + 2,
                config.heading.length()
        ) + (config.borderWidth * 2);

        int panel_height = shown_options
                         + (config.optionPadding * (shown_options - 1))
                         + (config.marginV * 2) 
                         + (config.borderWidth * 2);

        Panel option_panel = Panel.borderPanel(panel_width, panel_height, config.borderColor, config.bgColor);

        /************************************
         * Insert panel heading
         */
        if(config.heading != ""){
            //Don't allow multiline headings
            if(config.heading.indexOf('\n') >= 0){
                config.heading = config.heading.substring(0, config.heading.indexOf('\n'));
            }
            
            Panel p_heading = Panel.textPanel(config.heading, config.headingColor, config.borderColor);
            
            option_panel.insertCenterHorizontally(p_heading, 0);
        }

        /************************************
         * Add options to panel
         */

        for(int i = 0; i < shown_options; i++){
            int option_idx = i + list_offset;
            String option = new String(options[option_idx]);
            option = (option_idx == selection ? "> " : "  ") + option;
            option_panel.insert(
                    Panel.textPanel(
                            option, 
                            config.optionColor, 
                            config.bgColor), 
                    config.borderWidth + config.marginH, 
                    config.borderWidth + config.marginV + i * (config.optionPadding + 1)
            );
        }

        /************************************
         * Draw overflow helpers
         */
        boolean draw_up_arrow = list_offset > 0;
        boolean draw_down_arrow = (list_offset + shown_options) < options.length;

        if(draw_up_arrow){
            option_panel.insertCenterHorizontally(
                    Panel.textPanel("\u25B2", config.optionColor, config.bgColor),
                    1
            );
        }

        if(draw_down_arrow){
            option_panel.insertCenterHorizontally(
                    Panel.textPanel("\u25BC", config.optionColor, config.bgColor),
                    option_panel.height() - 2
            );
        }

        return option_panel;
    }
}
