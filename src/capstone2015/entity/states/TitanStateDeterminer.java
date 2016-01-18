package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.util.Util;
import java.util.List;
import java.util.stream.Collectors;

public class TitanStateDeterminer {
    
    public static final double SPECIAL_ATTACK_TIME = 6.5d;
    
    private Actor actor;
    private double attackTimeAccum = 0.d;
    
    public TitanStateDeterminer(Actor actor){
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
    
    public ActorStateSignature determineState(ActorState curState, double timeDelta){        
        Actor   closest_enemy                   = getClosestEnemy();
        boolean has_closest_enemy               = closest_enemy != null;
        Actor   leader                          = actor.getLeader();
        boolean has_leader                      = leader != null;
        double  actor_dist_to_leader            = Double.NEGATIVE_INFINITY;
        double  closest_enemy_dist_to_leader    = Double.NEGATIVE_INFINITY;
        
        if(     
               curState instanceof TitanMeteorShowerState
            && curState.isAlive()
        )
            return null;
        
        if(attackTimeAccum >= SPECIAL_ATTACK_TIME){
            attackTimeAccum = 0.d;
            return new ActorStateSignature(TitanMeteorShowerState.class, actor);
        }
        
        if(has_closest_enemy){
            attackTimeAccum += timeDelta;
            return new ActorStateSignature(MeleeAttackState.class, actor, closest_enemy);
        } else {
            attackTimeAccum = 0.d;
        }
        
        return new ActorStateSignature(DoNothingState.class, actor);
    }
}
