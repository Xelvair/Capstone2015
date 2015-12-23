package capstone2015.game.panel;

import capstone2015.entity.Item;
import capstone2015.game.Inventory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class InventoryPanel {
    public static Panel render(Inventory inventory, Color bgColor){
        Panel p = Panel.fillPanel(inventory.size() * 4, 1, new TerminalChar(' ', Color.WHITE, bgColor));
        
        for(int i = 0; i < inventory.size(); i++){
            Item item = inventory.get(i);
            
            if(item != null){
                p.set(i * 4, 0, new TerminalChar(Integer.toString(i).charAt(0), Color.WHITE, bgColor));
                p.set(i * 4 + 1, 0, new TerminalChar(':', Color.WHITE, bgColor));
                p.set(  i * 4 + 2, 0, 
                        new TerminalChar(
                            item.getRepresent().getCharacter(), 
                            item.getRepresent().getFGColor(), 
                            bgColor
                        )
                );
            }
        }
        
        return p;
    }
}
