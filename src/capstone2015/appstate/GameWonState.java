package capstone2015.appstate;

import capstone2015.game.GameMessage;
import capstone2015.state.State;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class GameWonState extends State{

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
                messageBus.enqueue(new Message(GameMessage.TERMINATE_GAME_STATE));
                terminate();
                break;
        }
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case GameMessage.KEY_EVENT:
                    onKeyEvent((Key)m.getMsgObject());
                    break;
            }
        }
        
        Panel p = Panel.borderPanel(30, 6, Color.YELLOW, Color.DARK_GRAY);
        p.insertCenterHorizontally(Panel.textPanel("YOU WON!", Color.YELLOW, Color.DARK_GRAY), 2);
        p.insertCenterHorizontally(Panel.textPanel("Press ENTER to Quit", Color.WHITE, Color.DARK_GRAY), 3);
        screen.insertCenter(p);
    }    
}
