package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import javax.swing.text.html.parser.Entity;

public interface OnHealBehavior {
    public void invoke(Actor entity, EntityBase source, int heal);
}
