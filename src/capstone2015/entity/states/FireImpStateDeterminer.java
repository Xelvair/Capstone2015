package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.util.Util;
import java.util.List;
import java.util.stream.Collectors;

public class FireImpStateDeterminer {
    private Actor actor;

    public FireImpStateDeterminer(Actor actor){
        this.actor = actor;
    }
    
    private Actor getClosestEnemy(){
        List<Actor> surrounding_enemies = 
                actor   .getView()
                        .getActors()
                        .stream()
                        .filter(a -> !a.isInvulnerable() && a.getTeamId() != actor.getTeamId())
                        .collect(Collectors.toList());
        
        surrounding_enemies.sort((Actor o1, Actor o2) -> {
            int o1_dist = actor.getPos().deltaOrthoMagnitude(o1.getPos());
            int o2_dist = actor.getPos().deltaOrthoMagnitude(o2.getPos());
            
            return Util.clamp(o1_dist - o2_dist, -1, 1);
        });
        
        if(surrounding_enemies.size() > 0){
            return surrounding_enemies.get(0);
        }
        
        return null;
    }
    
    public ActorStateSignature determineState(){
        Actor   closest_enemy                   = getClosestEnemy();
        boolean has_closest_enemy               = closest_enemy != null;
        Actor   leader                          = actor.getLeader();
        boolean has_leader                      = leader != null;
        double  actor_dist_to_leader            = Double.NEGATIVE_INFINITY;
        double  closest_enemy_dist_to_leader    = Double.NEGATIVE_INFINITY;
        
        if(has_leader)
            actor_dist_to_leader = actor.getPos().deltaMagnitude(leader.getPos());
        
        if(has_closest_enemy && has_leader)
            closest_enemy_dist_to_leader = closest_enemy.getPos().deltaMagnitude(leader.getPos());
        
        if(has_closest_enemy && closest_enemy_dist_to_leader <= actor.getOuterStray())
            return new ActorStateSignature(RangerAttackState.class, actor, closest_enemy);
        
        if(has_leader && actor_dist_to_leader > actor.getInnerStray())
            return new ActorStateSignature(GetInRangeOfState.class, actor, leader, actor.getInnerStray());
        
        return new ActorStateSignature(WanderingState.class, actor);
    }
}
