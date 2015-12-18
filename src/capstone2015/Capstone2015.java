package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateEvent;
import capstone2015.appstate.AppStateManager;
import capstone2015.game.Entity;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.Tile;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        Screen screen = new Screen();
        Map map = new Map();
        
        map.loadFromProperties("level.properties");

        
        
        asm.pushState(new AppState(){
            @Override
            protected void onTick(double timeDelta) {
                screen.insert(0, 0, Panel.fillPanel(screen.width(), screen.height(), new TerminalChar(' ', Color.BLACK, Color.BLACK)));
                screen.insert(0, 0, Panel.textPanel("hi", Color.WHITE, Color.CYAN));
                screen.insert(20, (int)(System.currentTimeMillis() % 1000000) / 1000 % 20, Panel.textPanel("Test123 123\nmultiline Textpanel", Color.BLUE, Color.GREEN));
                screen.insert(10, 3, Panel.fillPanel(10, 10, new TerminalChar('X', Color.MAGENTA, Color.RED)));
                
                screen.insert(0, 0, MapRenderer.render(map, new Recti(100, 100, 150, 50)));
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
            Key key;
            while((key = screen.readInput()) != null){
                switch(key.getKind()){
                    case Escape:
                        asm.terminateStates();
                        break;
                }
            }
            screen.flip();
            asm.tick(0.f);
            Thread.sleep(FRAME_TIME);
        }
        
        screen.close();
    }
}
