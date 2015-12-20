package capstone2015.appstate;

import capstone2015.game.panel.KeyPanels;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class KeyPage extends AppState{

    public static final int ENTITY_COUNT = 8;
    
    private Screen screen;
    private MessageBus messageBus;
    private int selection = 0;
    
    public KeyPage(Screen screen, MessageBus messageBus){
        this.screen = screen;
        this.messageBus = messageBus;
    }
    
    private void handleMessage(Message m){
        switch(m.getType()){
            case QuitToDesktop:
                terminate();
                break;
            case KeyEvent:
                Key key = (Key)m.getMsgObject();
                switch(key.getKind()){
                    case ArrowLeft:
                        selection += ENTITY_COUNT - 1;
                        selection %= ENTITY_COUNT;
                        break;
                    case ArrowRight:
                        ++selection;
                        selection %= ENTITY_COUNT;
                        break;
                    case Escape:
                        if(isFocus()){
                            terminate();
                        }
                        break;
                }
                break;
        }
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            handleMessage(m);
        }
        
        Panel p = Panel.borderPanel(62, 22, Color.WHITE, Color.BLACK);
        
        p.insertCenterHorizontally(Panel.textPanel("Use Arrow Keys to Navigate", Color.BLACK, Color.WHITE), 0);
        p.insert(Panel.textPanel("< Prev. Entity", Color.BLACK, Color.WHITE), 1, 21);
        p.insert(Panel.textPanel("Next Entity >", Color.BLACK, Color.WHITE), 48, 21);
        
        Panel p_entity = KeyPanels.render(selection);
        
        p.insert(p_entity, 1, 1);
        
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
