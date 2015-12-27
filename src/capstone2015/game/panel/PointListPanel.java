package capstone2015.game.panel;

import capstone2015.geom.Vec2i;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;
import java.util.LinkedList;

public class PointListPanel {
    public static Panel render(LinkedList<Vec2i> points){
        if(points.isEmpty()){
            return null;
        }
        
        int x_min = points.get(0).getX();
        int x_max = points.get(0).getX();
        int y_min = points.get(0).getY();
        int y_max = points.get(0).getY();
        
        for(Vec2i point : points){
            x_min = Math.min(x_min, point.getX());
            x_max = Math.max(x_max, point.getX());
            y_min = Math.min(y_min, point.getY());
            y_max = Math.max(y_max, point.getY());
        }
        
        int panel_width = (x_max - x_min) + 1;
        int panel_height = (y_max - y_min) + 1;
        
        Panel p = new Panel(panel_width, panel_height);
        p.fill(new TerminalChar());
        
        for(Vec2i point : points){
            p.set(point.getX() - x_min, point.getY() - y_min, new TerminalChar('X', Color.WHITE, Color.BLACK));
        }
        
        return p;
    }
}
