package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.graphics.TerminalChar;

public class DefaultOnTamedBehavior implements OnTamedBehavior{

    @Override
    public void invoke(Actor entity, Actor tamer, boolean wasSuccess) {
        if(wasSuccess){
            TerminalChar cur_entity_represent = new TerminalChar(entity.getRepresent());
            cur_entity_represent.setFGColor(tamer.getRepresent().getFGColor());
            entity.setRepresentOverride(cur_entity_represent);
        }
    }
}
