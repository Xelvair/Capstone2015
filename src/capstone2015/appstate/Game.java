package capstone2015.appstate;

import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.panel.NotificationPanel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

public class Game extends AppState{
    private Screen screen;
    private MessageBus messageBus;
    private Map map;

    
    public Game(Screen screen, MessageBus messageBus, String mapFile){
        this.screen = screen;
        this.messageBus = messageBus;
        map = new Map(messageBus);
        map.loadFromProperties(mapFile);
        map.resetPlayer(3, 3);
    }
    
    private void handleMessage(Message m){
        switch(m.getType()){
            case QuitToDesktop:
                terminate();
                break;
            case KeyEvent:
                Key key = (Key)m.getMsgObject();
                switch(key.getKind()){
                    case Escape:
                        if(isFocus()){
                            messageBus.enqueue(new Message(Message.Type.PushIngameMenuState));
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

        if(this.isFocus()){
            map.tick(timeDelta);
        } 
        
        screen.insert(NotificationPanel.render("Capstone2015 (C) Marvin Doerr", screen.width(), 1), 0, 0);
        screen.insert(MapRenderer.renderPlayerCentered(map, screen.width(), screen.height() - 1), 0, 1);
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
