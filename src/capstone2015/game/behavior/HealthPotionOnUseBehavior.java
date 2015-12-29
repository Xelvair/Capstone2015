package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.geom.Vec2i;

public class HealthPotionOnUseBehavior implements OnUseBehavior{

    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        //Directional use not allowed
        if(useDir.equals(new Vec2i(0, 0))) {
            user.heal(item, 3);
            item.terminate();
        }
    }
    
}
