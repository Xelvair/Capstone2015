package capstone2015.game.panel;

import capstone2015.entity.Item;
import capstone2015.game.Inventory;
import capstone2015.graphics.Panel;
import capstone2015.graphics.TerminalChar;
import java.awt.Color;

public class InventoryPanel {
    public static Panel render(Inventory inventory, Color bgColorSelected, Color bgColor){
        if(inventory.isEmpty()){
            return new Panel(0, 0);
        }
        
        Panel p_inv = Panel.fillPanel(inventory.size() * 4, 1, new TerminalChar(' ', Color.WHITE, bgColor));
        
        for(int i = 0; i < inventory.size(); i++){
            Item item = inventory.get(i);
            
            if(item != null){
                Color real_bg_color;
                if(inventory.getSelectIndex() == i){
                    real_bg_color = bgColorSelected;
                } else {
                    real_bg_color = bgColor;
                }
                p_inv.set(i * 4, 0, new TerminalChar(Integer.toString((i + 1) % 9).charAt(0), Color.WHITE, real_bg_color));
                p_inv.set(i * 4 + 1, 0, new TerminalChar(':', Color.WHITE, real_bg_color));
                p_inv.set(  i * 4 + 2, 0, 
                        new TerminalChar(
                            item.getRepresent().getCharacter(), 
                            item.getRepresent().getFGColor(), 
                            real_bg_color
                        )
                );
            }
        }
        
        String helpstring = "";
        
        Item sel_item = inventory.getSelectedItem();
        if(sel_item != null){
            helpstring += "'Q' to drop";
            if(sel_item.isUsable()){
                helpstring += ", 'F' to use";
            }
            helpstring += ", ";
        }
        helpstring += "'1' to '" + inventory.size() + "' to select";
        
        Panel p_help = Panel.textPanel(helpstring, Color.WHITE, Color.DARK_GRAY);
        
        Panel p = new Panel(p_inv.width() + p_help.width(), 1);
        
        p.insert(p_inv, 0, 0);
        p.insert(p_help, p_inv.width(), 0);
        
        return p;
    }
}
