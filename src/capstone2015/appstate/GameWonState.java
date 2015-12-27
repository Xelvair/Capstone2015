package capstone2015.appstate;

import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import static capstone2015.messaging.Message.Type.TerminateGameState;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class GameWonState extends AppState{

    private Screen screen;
    private MessageBus messageBus;
    
    public GameWonState(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;
    }
    
    private void onKeyEvent(Key key){
        switch(key.getKind()){
            case Escape:
            case Enter:
                messageBus.enqueue(new Message(TerminateGameState));
                terminate();
                break;
        }
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case KeyEvent:
                    onKeyEvent((Key)m.getMsgObject());
                    break;
            }
        }
        
        Panel p = Panel.borderPanel(30, 6, Color.YELLOW, Color.DARK_GRAY);
        p.insertCenterHorizontally(Panel.textPanel("YOU WON!", Color.YELLOW, Color.DARK_GRAY), 2);
        p.insertCenterHorizontally(Panel.textPanel("Press ENTER to Quit", Color.WHITE, Color.DARK_GRAY), 3);
        screen.insertCenter(p);
    }

    @Override
    protected void onEvent(AppStateEvent event) {
        switch(event){
            case TERMINATE:
                terminate();
                break;
        }
    }
    
}
