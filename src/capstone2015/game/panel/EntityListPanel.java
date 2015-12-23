package capstone2015.game.panel;

import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import capstone2015.util.Util;
import java.awt.Color;
import java.util.ArrayList;

import capstone2015.entity.EntityBase;
public class EntityListPanel {
    public static class Config{
        public int marginV = 1;
        public int marginH = 1;
        public String title = "";
        public String subtitle = "";
        public Color bgColor = Color.BLACK;
        public Color borderColor = Color.WHITE;
    }
    
    public static Panel render(ArrayList<? extends EntityBase> entities, Config config){
        ArrayList<String> entity_names = new ArrayList<>();
        for(EntityBase entity : entities){
            entity_names.add(entity.getName());
        }
        
        int longest_entity_name = Util.maxLength(entity_names);
        
        int max_title_length = Math.max(config.title.length(), config.subtitle.length());
        
        int pwidth_inner = 2 + longest_entity_name;
        int pheight_inner = entities.size();
        int pwidth = 2 + Math.max(pwidth_inner + 2 * config.marginH, max_title_length);
        int pheight = 2 + 2 * config.marginV + pheight_inner;
        
        Panel p_inner = Panel.fillPanel(pwidth_inner, pheight_inner, new TerminalChar().setBGColor(config.bgColor));
        
        for(int i = 0; i < entities.size(); i++){
            EntityBase entity = entities.get(i);
            p_inner.set(0, i, new TerminalChar(entity.getRepresent()).setBGColor(config.bgColor));
            p_inner.insert(Panel.textPanel(entity.getName(), Color.WHITE, config.bgColor), 2, i);
        }
        
        Panel p = Panel.borderPanel(pwidth, pheight, config.borderColor, config.bgColor);
        
        p.insertCenterHorizontally(Panel.textPanel(config.title, Color.WHITE, config.borderColor), 0);
        p.insertCenterHorizontally(Panel.textPanel(config.subtitle, Color.WHITE, config.borderColor), p.height() - 1);
        
        p.insertCenter(p_inner);
        
        return p;
    }
}
