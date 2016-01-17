package capstone2015.appstate;

import capstone2015.game.GameMessage;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.state.State;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class AlertState extends State{

    private Screen screen;
    private MessageBus messageBus;
    private String message;
    
    public AlertState(Screen screen, MessageBus messageBus, String message){
        this.screen = screen;
        this.messageBus = messageBus;
        this.message = message;
    }
    
    @Override
    public void onTick(double timeDelta) {
        if(isBlur())
            return;
        
        for(Message m : messageBus){
            switch(m.getType()){
                case GameMessage.KEY_EVENT:
                {
                    Key key = (Key)m.getMsgObject();
                    switch(key.getKind()){
                        case Escape:
                        case Enter:
                            terminate();
                            break;
                    }
                }
            }
        }
        
        Panel p_message = Panel.textPanel(message, Color.WHITE, Color.DARK_GRAY);
        
        Panel p = Panel.borderPanel(p_message.width() + 4, p_message.height() + 4, Color.WHITE, Color.DARK_GRAY);
        
        p.insertCenter(p_message);
        
        p.insertCenterHorizontally(Panel.textPanel("Enter to Accept", Color.BLACK, Color.WHITE), p.height() - 1);
        
        screen.insertCenter(p);
    }
}
