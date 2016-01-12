package capstone2015.appstate;

import capstone2015.diagnostics.TimeStat;
import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.MaskedMapView;
import capstone2015.game.NotificationList;
import capstone2015.game.panel.HudPanel;
import capstone2015.game.panel.EntityListPanel;
import capstone2015.game.panel.NotificationPanel;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.messaging.PushNotificationParams;
import capstone2015.messaging.ReceivedDamageParams;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;
import java.util.ArrayList;

public class Game extends AppState{
    public static final int NOTIFICATION_LIST_SIZE = 2;
    public static final int HUD_HEIGHT = 1;
    
    private final Screen screen;
    private final MessageBus messageBus;
    private final Map map;
    private final NotificationList notifications = new NotificationList(NOTIFICATION_LIST_SIZE);
    private final MaskedMapView maskedMapView;

    
    public Game(Screen screen, MessageBus messageBus, String mapFile){
        this.screen = screen;
        this.messageBus = messageBus;
        map = new Map(messageBus);
        map.loadFromProperties(mapFile);
        maskedMapView = new MaskedMapView(map);
        notifications.push("You enter the dungeon...", Color.YELLOW);
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
                onReceivedDamage((ReceivedDamageParams)m.getMsgObject());
                break;
            case TerminateGameState:
                terminate();
                break;
            case PushNotification:
                onPushNotification((PushNotificationParams)m.getMsgObject());
                break;
            case GameWon:
                notifications.push("You unlock the door and exit the dungeon!", Color.YELLOW);
                break;
            case SaveGame:
                map.storeToProperties((String)m.getMsgObject());
                break;
            case Terminate:
            {
                if(m.getMsgObject() == map.getPlayer()){
                    notifications.push("You died.", Color.RED);
                }
            }
                
        }
    }
    
    private void onReceivedDamage(ReceivedDamageParams msg_obj){
        /********************************************
         * Check if the player was damaged
         */
        if(msg_obj.damagedActor == map.getPlayer()) {
            String notif_text = String.format(
                    "You take %d damage from %s!",
                    msg_obj.damage,
                    msg_obj.damagingEntity.getName()
            );
            Color notif_color = msg_obj.damagingEntity.getRepresent().getFGColor();
            notifications.push(notif_text, notif_color);
            return;
        }

        /********************************************
         * Check if an entity was damaged by the player directly
         */
        if(msg_obj.damagingEntity == map.getPlayer()){
            String notif_text = String.format(
                    "You inflict %d damage on %s!",
                    msg_obj.damage,
                    msg_obj.damagedActor.getName()
            );
            Color notif_color = msg_obj.damagingEntity.getRepresent().getFGColor();
            notifications.push(notif_text, notif_color);
            return;
        }

        /********************************************
         * Check if an entity was damaged by the player indirectly
         */
        EntityBase parent = msg_obj.damagingEntity.getParent();
        while(parent != null){
            if(parent == map.getPlayer()){
                String notif_text = String.format(
                        "You inflict %d damage on %s!",
                        msg_obj.damage,
                        msg_obj.damagedActor.getName()
                );
                Color notif_color = parent.getRepresent().getFGColor();
                notifications.push(notif_text, notif_color);
                return;
            }
            parent = parent.getParent();
        }
    }

    private void onPushNotification(PushNotificationParams pnp){
        notifications.push(pnp.notification, pnp.color);
    }
    
    @Override
    protected void onTick(double timeDelta) {
        for(Message m : messageBus){
            handleMessage(m);
        }
        
        if(this.isFocus()){
            TimeStat.enterState("AI.Tick");
            map.tick(timeDelta);
            TimeStat.leaveState("AI.Tick");
        } 
        
        Panel p_notif;
        p_notif = NotificationPanel.render(notifications, screen.width());
        screen.insert(p_notif, 0, 0);
        
        Actor player = map.getPlayer();
        Recti map_render_rect = getPlayerRenderRect();
        
        screen.insert(MapRenderer.render(player.getView(), map_render_rect), 0, NOTIFICATION_LIST_SIZE);
        
        drawPickupableList();
        
        Panel p_hud;
        Actor plr = map.getPlayer();
        p_hud = HudPanel.render(plr.getHealth(), plr.getMaxHealth(), plr.getInventory(), screen.width());
        screen.insert(p_hud, 0, screen.height() - 1);
    }
    
    private Recti getPlayerRenderRect(){
        int left = 0;
        int top = 0;
        int width = screen.width();
        int height = screen.height() - NOTIFICATION_LIST_SIZE - HUD_HEIGHT;

        Actor player = null;
        if((player = map.getPlayer()) != null){
            left = player.getXPos() - width / 2;
            top = player.getYPos() - height / 2;
        }
        
        return new Recti(left, top, width, height);
    }
    
    private void drawPickupableList(){
        Actor player = map.getPlayer();
        if(player != null){
            ArrayList<? extends EntityBase> pickupable_items = map.getPickupableAt(player.getPos());
            if(!pickupable_items.isEmpty()){
                EntityListPanel.Config elist_p_cfg = new EntityListPanel.Config();
                elist_p_cfg.bgColor = Color.BLACK;
                elist_p_cfg.borderColor = Color.DARK_GRAY;
                elist_p_cfg.marginH = 1;
                elist_p_cfg.marginV = 1;
                elist_p_cfg.title = "'E' to pick up";
                elist_p_cfg.subtitle = "";
                
                Panel p_pickupable = EntityListPanel.render(pickupable_items, elist_p_cfg);
                screen.insert(p_pickupable, 0, screen.height() - p_pickupable.height());
            }
        }
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
