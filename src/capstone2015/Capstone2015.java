package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateEvent;
import capstone2015.appstate.AppStateManager;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        Screen screen = new Screen();
        Map map = new Map();
        
        map.loadFromProperties("level.properties");
        
        asm.pushState(new AppState(){
            private int render_x = 0;
            private int render_y = 0;
            @Override
            protected void onTick(double timeDelta) {
                screen.insert(Panel.fillPanel(screen.width(), screen.height(), new TerminalChar(' ', Color.BLACK, Color.BLACK)), 0, 0);
                screen.insert(Panel.textPanel("hi", Color.WHITE, Color.CYAN), 0, 0);
                screen.insert(Panel.textPanel("Test123 123\nmultiline Textpanel", Color.BLUE, Color.GREEN), 20, (int)(System.currentTimeMillis() % 1000000) / 1000 % 20);
                screen.insert(Panel.textPanel("Test123 123\nmultiline Textpanel", Color.BLUE, Color.GREEN), 10, 3);
                
                screen.insert(MapRenderer.render(map, new Recti(render_x, render_y, screen.width(), screen.height())), 0, 0);
                
                Panel guipanel = Panel.fillPanel(10, 10, new TerminalChar('X', Color.BLACK, Color.WHITE));
                guipanel.insertCenter(Panel.fillPanel(8, 8, new TerminalChar(' ', Color.WHITE, Color.BLACK)));
                screen.insertCenter(guipanel);
                
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
       
        while(!asm.isEmpty()){
            /*Key key;
            while((key = screen.readInput()) != null){
                switch(key.getKind()){
                    case Escape:
                        asm.terminateStates();
                        break;
                }
            }*/
            screen.flip();
            asm.tick(0.f);
            Thread.sleep(FRAME_TIME);
        }
        
        screen.close();
    }
}
