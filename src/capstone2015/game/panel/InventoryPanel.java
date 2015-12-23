package capstone2015.game.panel;

import capstone2015.entity.Item;
import capstone2015.game.Inventory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class InventoryPanel {
    public static Panel render(Inventory inventory, Color bgColorSelected, Color bgColor){
        Panel p = Panel.fillPanel(inventory.size() * 4, 1, new TerminalChar(' ', Color.WHITE, bgColor));
        
        for(int i = 0; i < inventory.size(); i++){
            Item item = inventory.get(i);
            
            if(item != null){
                Color real_bg_color;
                if(inventory.getSelectIndex() == i){
                    real_bg_color = bgColorSelected;
                } else {
                    real_bg_color = bgColor;
                }
                p.set(i * 4, 0, new TerminalChar(Integer.toString((i + 1) % 9).charAt(0), Color.WHITE, real_bg_color));
                p.set(i * 4 + 1, 0, new TerminalChar(':', Color.WHITE, real_bg_color));
                p.set(  i * 4 + 2, 0, 
                        new TerminalChar(
                            item.getRepresent().getCharacter(), 
                            item.getRepresent().getFGColor(), 
                            real_bg_color
                        )
                );
            }
        }
        
        return p;
    }
}
