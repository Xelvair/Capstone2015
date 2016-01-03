package capstone2015.game.panel;

import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Pair;

import java.awt.*;
import java.util.LinkedList;

public class HelpPanel {
    public static int WIDTH = 62;
    public static int HEIGHT = 20;

    public int selectedPanel = 0;
    public int scroll = 0;

    private LinkedList<Pair<String, Panel>> panels = new LinkedList<>();

    public void addPanel(String panelName, Panel panelContent){
        panels.add(new Pair<>(panelName, panelContent));
    }

    public Panel render(){
        Panel p = Panel.borderPanel(WIDTH, HEIGHT, Color.WHITE, Color.DARK_GRAY);

        p.insertCenterHorizontally(Panel.textPanel("Help", Color.BLACK, Color.WHITE), 0);

        Panel p_panel_select = Panel.fillPanel(WIDTH - 2, 1, new TerminalChar(' ', Color.WHITE, Color.LIGHT_GRAY));

        int x_accum = 0;
        for(int i = 0; i < panels.size(); ++i){
            String panel_name = " " + panels.get(i).getFirst() + " ";
            Color fg_color = (i == selectedPanel ? Color.WHITE : Color.BLACK);
            Color bg_color = (i == selectedPanel ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            Panel name_panel = Panel.textPanel(panel_name, fg_color, bg_color);
            p_panel_select.insert(name_panel, x_accum, 0);
            x_accum += name_panel.width();

            if(i == selectedPanel){
                Panel content_panel = panels.get(i).getSecond();
                Recti content_frame_rect = new Recti(0, scroll, 60, 17);
                Recti content_rect = new Recti(0, 0, content_panel.width(), content_panel.height());
                Recti intersect = content_frame_rect.intersect(content_rect);
                p.insert(content_panel.subArray(intersect), 1, 2);
            }
        }
        p.insert(p_panel_select, 1, 1);
        return p;
    }

    public void nextPanel(){
        selectedPanel += 1;
        selectedPanel %= panels.size();
        scroll = 0;
    }

    public void prevPanel(){
        selectedPanel += panels.size() - 1;
        selectedPanel %= panels.size();
        scroll = 0;
    }

    public void scrollDown(){
        if(scroll < panels.get(selectedPanel).getSecond().height() - 17)
            scroll++;
    }

    public void scrollUp(){
        if(scroll > 0)
            scroll--;
    }

}
