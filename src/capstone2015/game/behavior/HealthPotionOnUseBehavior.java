package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;

public class HealthPotionOnUseBehavior implements OnUseBehavior{

    @Override
    public void invoke(Item item, Actor user) {
        user.heal(item, 3);
        item.terminate();
    }
    
}
