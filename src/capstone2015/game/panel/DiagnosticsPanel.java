package capstone2015.game.panel;

import capstone2015.diagnostics.TimeStat;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;
import java.util.Map;

public class DiagnosticsPanel {
    public static final int PANEL_WIDTH = 60;
    public static final int BORDER_WIDTH = 1;
    public static final int PANEL_INNER_PADDING = 1;
    public static final int PANEL_INNER_WIDTH = PANEL_WIDTH - BORDER_WIDTH * 2;
    public static final int PANEL_CONTENT_WIDTH = PANEL_INNER_WIDTH - PANEL_INNER_PADDING * 2;
    public static final Color[] diag_colors = {
        Color.GREEN,
        Color.RED,
        Color.YELLOW,
        Color.BLUE,
        Color.CYAN,
        Color.YELLOW,
        Color.WHITE,
        Color.MAGENTA,
        Color.PINK,
        Color.GRAY
    };
    
    public static Panel render(){
        long elapsed_time = TimeStat.getElapsedTime();
        Map<String, Long> state_summary = TimeStat.getStateSummary();
        
        Panel p = Panel.borderPanel(PANEL_WIDTH, state_summary.size() + 4, Color.WHITE, Color.BLACK);
        
        /**************
         * On the first pass, get max name length and max time
         */
        int longest_state_name = 0;
        long max_state_time = 0L;
        for(String state : state_summary.keySet()){
            longest_state_name = Math.max(longest_state_name, state.length());
            max_state_time = Math.max(max_state_time, state_summary.get(state));
        }
        
        /*************
         * On the second pass, draw diags
         */
        int state_idx = 0;
        for(String state : state_summary.keySet()){
            p.insert(
                    Panel.textPanel(state, Color.WHITE, Color.BLACK),
                    BORDER_WIDTH + PANEL_INNER_PADDING, 
                    BORDER_WIDTH + PANEL_INNER_PADDING + state_idx
            );
            
            int diag_start_x = BORDER_WIDTH + PANEL_INNER_PADDING + longest_state_name + 1;
            int diag_max_width = PANEL_INNER_WIDTH - PANEL_INNER_PADDING * 2 - longest_state_name - 1;
            
            int diag_width = (int)(((float)state_summary.get(state) / (float)max_state_time) * (float)diag_max_width);
            
            p.insert(
                    Panel.fillPanel(diag_width, 1, new TerminalChar(' ', Color.WHITE, diag_colors[state_idx])),
                    diag_start_x, 
                    BORDER_WIDTH + PANEL_INNER_PADDING + state_idx
            );
            
            ++state_idx;
        }
        
        p.insertCenterHorizontally(Panel.textPanel("Elapsed time: " + elapsed_time / 1000000 + " msec."), p.height() - 1);
        
        return p;
    }
}
