package capstone2015.appstate;

import capstone2015.game.Entity;
import capstone2015.game.EntityProto;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.NotificationList;
import capstone2015.game.panel.HudPanel;
import capstone2015.game.panel.NotificationPanel;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.messaging.ReceiveDamageParams;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;

public class Game extends AppState{
    public static final int NOTIFICATION_LIST_SIZE = 2;
    public static final int HUD_HEIGHT = 1;
    
    private Screen screen;
    private MessageBus messageBus;
    private Map map;
    private NotificationList notifications = new NotificationList(NOTIFICATION_LIST_SIZE);

    
    public Game(Screen screen, MessageBus messageBus, String mapFile){
        this.screen = screen;
        this.messageBus = messageBus;
        map = new Map(messageBus);
        map.loadFromProperties(mapFile);
        map.resetPlayer(4, 3);
        notifications.push("CapStone2015 (C) Marvin Doerr", Color.YELLOW);
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
            case ReceivedDamage:
            {
                ReceiveDamageParams msg_obj = (ReceiveDamageParams)m.getMsgObject();
                if(msg_obj.getDamagedEntity() == map.getPlayer()){
                    EntityProto e_damager = EntityProto.get(msg_obj.getDamagingEntity().getId());
                    String notif_text = String.format(
                            "You take %d damage from %s!", 
                            msg_obj.getDamage(),
                            e_damager.getName()
                    );
                    Color notif_color = e_damager.getRepresentVisible().getFGColor();
                    notifications.push(notif_text, notif_color);
                }
                break;
            }
            case PlayerEncounter:
            {
                Entity e_enc = (Entity)m.getMsgObject();
                EntityProto e_enc_proto = EntityProto.get(e_enc.getId());
                String entity_name_str = e_enc_proto.getName();
                String notif_text = String.format("You encounter a %s!", entity_name_str);
                Color notif_color = e_enc_proto.getRepresentVisible().getFGColor();
                notifications.push(notif_text, notif_color);
                break;
            }
            case Terminate:
            {
                if(m.getMsgObject() == map.getPlayer()){
                    notifications.push("You died.", Color.RED);
                }
            }
                
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
        
        Panel p_notif;
        p_notif = NotificationPanel.render(notifications, screen.width());
        screen.insert(p_notif, 0, 0);

        Panel p_hud;
        p_hud = HudPanel.render(map.getPlayer(), screen.width());
        screen.insert(p_hud, 0, screen.height() - 1);
        
        screen.insert(MapRenderer.renderPlayerCentered(map, screen.width(), screen.height() - NOTIFICATION_LIST_SIZE - HUD_HEIGHT), 0, NOTIFICATION_LIST_SIZE);
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
