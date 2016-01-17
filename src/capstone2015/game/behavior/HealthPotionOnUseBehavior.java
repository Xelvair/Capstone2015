package capstone2015.game.behavior;

import capstone2015.entity.Actor;
import capstone2015.entity.Item;
import capstone2015.geom.Vec2i;
import java.util.List;
import java.util.stream.Collectors;

public class HealthPotionOnUseBehavior implements OnUseBehavior{

    @Override
    public void invoke(Item item, Actor user, Vec2i useDir) {
        //Directional use not allowed
        if(useDir.equals(new Vec2i(0, 0))) {
            user.heal(item, 3);
            item.terminate();
        } else {
            List<Actor> use_targets = 
                user.getView()
                    .getActorsAt(user.getPos().add(useDir))
                    .stream()
                    .filter(a -> !a.isInvulnerable() && a.getTeamId() == user.getTeamId())
                    .collect(Collectors.toList());
            
            if(use_targets.size() >= 1){
                Actor use_target = use_targets.get(0);
                
                if(use_target.getLeader() == user){
                    use_target.healMax(item);
                } else {
                    use_target.heal(item, 3);
                }
                item.terminate();
            }
        }
    }
}
