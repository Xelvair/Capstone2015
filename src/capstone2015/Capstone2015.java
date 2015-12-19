package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateEvent;
import capstone2015.appstate.AppStateManager;
import capstone2015.game.Entity;
import capstone2015.game.PositionedEntity;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.panel.NotificationPanel;
import capstone2015.geom.Recti;
import capstone2015.graphics.Screen;
import com.googlecode.lanterna.input.Key;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        Screen screen = new Screen();
        Map map = new Map();
        
        map.loadFromProperties("level.properties");
        
        map.resetPlayer(3, 3);
        map.add(new PositionedEntity(Entity.ID_KEY, 3, 3));
        
        System.out.println(map.getEntitiesAt(3, 3).size());
        
        asm.pushState(new AppState(){
            private int render_x = 0;
            private int render_y = 0;
            @Override
            protected void onTick(double timeDelta) {
                map.tick(timeDelta);
                
                screen.insert(new NotificationPanel("Capstone2015 (C) Marvin Doerr", screen.width(), 1), 0, 0);
                screen.insert(MapRenderer.renderPlayerCentered(map, screen.width(), screen.height()), 0, 0);
                
                Key key;
                while((key = screen.readInput()) != null){
                    switch(key.getKind()){
                        case ArrowLeft:
                            render_x -= 5;
                            break;
                        case ArrowRight:
                            render_x += 5;
                            break;
                        case ArrowUp:
                            render_y -= 5;
                            break;
                        case ArrowDown:
                            render_y += 5;
                            break;
                        case Escape:
                            terminate();
                            break;
                    }
                }
            }

            @Override
            protected void onEvent(AppStateEvent event) {
                if(event == AppStateEvent.TERMINATE){
                    terminate();
                }
                System.out.println("event: " + event.toString());
            }
            
        }); 
       
        long lastClock = System.currentTimeMillis();
        while(!asm.isEmpty()){
            long deltatime_msec = System.currentTimeMillis() - lastClock;
            lastClock = System.currentTimeMillis();
            screen.flip();
            asm.tick((double)deltatime_msec / 1000.d);
            Thread.sleep(FRAME_TIME);
        }
        
        screen.close();
    }
}
