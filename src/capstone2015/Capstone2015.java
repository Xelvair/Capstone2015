package capstone2015;

import capstone2015.appstate.AppState;
import capstone2015.appstate.AppStateEvent;
import capstone2015.appstate.AppStateManager;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.Tile;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class Capstone2015 {
    
    public static final int FRAME_RATE = 60;
    public static final int FRAME_TIME = 1000 / FRAME_RATE;
    
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        
        Screen screen = new Screen();
        
        asm.pushState(new AppState(){
            @Override
            protected void onTick(double timeDelta) {
                screen.insert(0, 0, Panel.textPanel("hi", Color.WHITE, Color.CYAN));
                screen.insert(20, 20, Panel.textPanel("Test123 123\nmultiline Textpanel", Color.BLUE, Color.GREEN));
                screen.insert(10, 3, Panel.fillPanel(10, 10, new Tile('X', Color.MAGENTA, Color.RED)));
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
