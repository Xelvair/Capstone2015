package capstone2015.entity.states;

import capstone2015.entity.Actor;
import capstone2015.entity.EntityFactory;
import capstone2015.game.GameMessage;
import capstone2015.geom.Vec2i;
import capstone2015.messaging.Message;
import capstone2015.messaging.SpawnActorParams;
import java.util.Random;
import java.util.TreeMap;

public class TitanMeteorShowerState extends ActorState{
    
    public static final double  METEOR_SPAWN_TIME = 0.075d;
    public static final double  TITAN_METEOR_DURATION = 2.d;
    public static final int     METEOR_SPAWN_SPREAD = 10;
    
    double duration;
    double meteorSpawnTimer;
    
    public TitanMeteorShowerState(Actor actor){
        super(actor);
        
        duration = TITAN_METEOR_DURATION;
    }

    @Override
    public void onTick(double timeDelta) {
        if(duration <= 0.d){
            terminate();
            return;
        }
        
        while(meteorSpawnTimer > METEOR_SPAWN_TIME){
            Random rand = new Random();
            
            Vec2i random_offset = new Vec2i(
                    rand.nextInt(METEOR_SPAWN_SPREAD * 2) - METEOR_SPAWN_SPREAD,
                    rand.nextInt(METEOR_SPAWN_SPREAD * 2) - METEOR_SPAWN_SPREAD
            );
            
            Vec2i meteor_pos = getActor().getPos().add(random_offset);
            
            Vec2i[] meteor_particle_positions = new Vec2i[]{
                meteor_pos,
                meteor_pos.add(new Vec2i(1, 0)),
                meteor_pos.add(new Vec2i(-1, 0)),
                meteor_pos.add(new Vec2i(0, 1)),
                meteor_pos.add(new Vec2i(0, -1)),
            };
            
            for(int i = 0; i < meteor_particle_positions.length; ++i){
                SpawnActorParams sap = new SpawnActorParams();
                sap.entityId = EntityFactory.ID_METEOR;
                sap.parent = getActor();
                sap.position = meteor_particle_positions[i];
                
                sap.instantiationParams = new TreeMap();
                sap.instantiationParams.put("TeamIdOverride", getActor().getTeamId());
                
                getActor().sendBusMessage(new Message(GameMessage.SPAWN_ACTOR, sap));
            }

            meteorSpawnTimer -= METEOR_SPAWN_TIME;
        }
        
        meteorSpawnTimer += timeDelta;
        duration -= timeDelta;
    }
}
