package capstone2015.game;

import capstone2015.entity.Actor;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.util.Util;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ActorEventGenerator {
    private Actor actor;
    private MessageBus messageBus;
    
    private boolean previouslyInInnerStray = true;
    private boolean previouslyInOuterStray = true;
    private List<Actor> actorsInVision = new LinkedList();
    private List<Actor> actorsInOuterStrayRange = new LinkedList();
    private List<Actor> actorsInInnerStrayRange = new LinkedList();
    
    public ActorEventGenerator(Actor actor, MessageBus messageBus){
        this.actor = actor;
        this.messageBus = messageBus;
    }
    
    /**************************
    * Detect new closest enemy
    */
    private void checkClosestEnemy(){
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
            Actor closest_enemy = surrounding_enemies.get(0);
            
            if(actor.getLeader() != null){
                Actor leader = actor.getLeader();
                if(leader.getPos().deltaMagnitude(closest_enemy.getPos()) > actor.getOuterStray()){
                    return;
                }
            }
            
            messageBus.enqueue(new Message(ActorMessage.CLOSEST_ENEMY_TICK, closest_enemy));
            
            if(actor.getTarget() == null || closest_enemy != actor.getTarget()){
                messageBus.enqueue(new Message(ActorMessage.NEW_CLOSEST_ENEMY, closest_enemy));
            }
        }
    }
    
    /*******************
     * Detect whether actors left or entered our vision
     */
    private void checkEnterLeaveVision(){
        /**************************
         * Detect actors that entered the vision
         */
        for(Actor a : actor.getView().getActors()){
            if(!actorsInVision.contains(a) && a != actor){
                actorsInVision.add(a);
                messageBus.enqueue(new Message(ActorMessage.ACTOR_ENTERED_VISION, a));
            }
        }
        
        /**************************
         * Detect actors that left the vision
         */
        Iterator<Actor> it = actorsInVision.iterator();
        List<Actor> actors_currently_in_view = actor.getView().getActors();
        while(it.hasNext()){
            Actor a = it.next();
            if(!actors_currently_in_view.contains(a)){
                it.remove();
                messageBus.enqueue(new Message(ActorMessage.ACTOR_LEFT_VISION, a));
            }
        }
    }
    
    /**************************
     * Check if actor left stray ranges
     */
    private void checkStrayRanges(){
        if(actor.getLeader() != null){
            double distance = actor.getPos().deltaMagnitude(actor.getLeader().getPos());
            
            boolean is_in_inner_stray = distance <= actor.getInnerStray();
            boolean is_in_outer_stray = distance <= actor.getOuterStray();
            
            if(!is_in_inner_stray && previouslyInInnerStray){
                previouslyInInnerStray = false;
                messageBus.enqueue(new Message(ActorMessage.SELF_LEFT_INNER_STRAY));
            }
            
            if(!is_in_outer_stray && previouslyInOuterStray){
                previouslyInOuterStray = false;
                messageBus.enqueue(new Message(ActorMessage.SELF_LEFT_OUTER_STRAY));
            }
            
            if(is_in_inner_stray && !previouslyInInnerStray){
                previouslyInInnerStray = true;
                messageBus.enqueue(new Message(ActorMessage.SELF_ENTERED_INNER_STRAY));
            }
            
            if(is_in_outer_stray && !previouslyInOuterStray){
                previouslyInOuterStray = true;
                messageBus.enqueue(new Message(ActorMessage.SELF_ENTERED_OUTER_STRAY));
            }
        }
    }
    
    private void checkEnterLeaveInnerStray(){
        List<Actor> in_prev_inner_stray = actorsInInnerStrayRange;
        List<Actor> in_cur_inner_stray = actor.getView().getActors().stream().filter(
                a -> a.getPos().deltaMagnitude(actor.getPos()) < actor.getInnerStray()
        ).collect(Collectors.toList());
        
        /**************************
         * Detect actors that entered the inner stray range
         */
        Iterator<Actor> it_in_cur_inner_stray = in_cur_inner_stray.iterator();
        
        while(it_in_cur_inner_stray.hasNext()){
            Actor a = it_in_cur_inner_stray.next();
            if(!in_prev_inner_stray.contains(a) && a != actor){
                in_prev_inner_stray.add(a);
                messageBus.enqueue(new Message(ActorMessage.ACTOR_ENTERED_INNER_STRAY, a));
            }
        }
        
        /**************************
         * Detect actors that left the inner stray range
         */
        Iterator<Actor> it_in_prev_inner_stray = in_prev_inner_stray.iterator();
        
        while(it_in_prev_inner_stray.hasNext()){
            Actor a = it_in_prev_inner_stray.next();
            if(!in_cur_inner_stray.contains(a)){
                it_in_prev_inner_stray.remove();
                messageBus.enqueue(new Message(ActorMessage.ACTOR_LEFT_INNER_STRAY, a));
            }
        }
    }
    
    private void checkEnterLeaveOuterStray(){
        List<Actor> in_prev_outer_stray = actorsInOuterStrayRange;
        
        List<Actor> in_cur_outer_stray = actor.getView().getActors().stream().filter(
                a -> a.getPos().deltaMagnitude(actor.getPos()) < actor.getOuterStray()
        ).collect(Collectors.toList());
        
        /**************************
         * Detect actors that entered the outer stray range
         */
        
        Iterator<Actor> it_in_cur_outer_stray = in_cur_outer_stray.iterator();
        
        while(it_in_cur_outer_stray.hasNext()){
            Actor a = it_in_cur_outer_stray.next();
            if(!in_prev_outer_stray.contains(a) && a != actor){
                in_prev_outer_stray.add(a);
                messageBus.enqueue(new Message(ActorMessage.ACTOR_ENTERED_OUTER_STRAY, a));
            }
        }
        
        /**************************
         * Detect actors that left the inner stray range
         */
        
        Iterator<Actor> it_in_prev_outer_stray = in_prev_outer_stray.iterator();
        
        while(it_in_prev_outer_stray.hasNext()){
            Actor a = it_in_prev_outer_stray.next();
            if(!in_cur_outer_stray.contains(a)){
                it_in_prev_outer_stray.remove();
                messageBus.enqueue(new Message(ActorMessage.ACTOR_LEFT_OUTER_STRAY, a));
            }
        }
    }
    
    public void tick(){
        checkClosestEnemy();
        checkStrayRanges();
        checkEnterLeaveVision();
        checkEnterLeaveOuterStray();
        checkEnterLeaveInnerStray();
    }
}
