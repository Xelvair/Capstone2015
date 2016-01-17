package capstone2015.appstate;

import capstone2015.state.State;
import capstone2015.diagnostics.TimeStat;
import capstone2015.entity.Actor;
import capstone2015.entity.EntityBase;
import capstone2015.entity.EntityFactory;
import capstone2015.game.GameMessage;
import capstone2015.game.Map;
import capstone2015.game.MapRenderer;
import capstone2015.game.NotificationList;
import capstone2015.game.TutorialTracker;
import capstone2015.game.panel.HudPanel;
import capstone2015.game.panel.EntityListPanel;
import capstone2015.game.panel.FollowerPanel;
import capstone2015.game.panel.NotificationPanel;
import capstone2015.geom.Recti;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import capstone2015.messaging.PickedUpParams;
import capstone2015.messaging.PushNotificationParams;
import capstone2015.messaging.ReceivedDamageParams;
import capstone2015.messaging.TamedParams;
import com.googlecode.lanterna.input.Key;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GameState extends State{
    public static final int NOTIFICATION_LIST_SIZE = 2;
    public static final int HUD_HEIGHT = 1;
    
    private final Screen screen;
    private final MessageBus messageBus;
    private final Map map;
    private final NotificationList notifications = new NotificationList(NOTIFICATION_LIST_SIZE);
    private final TutorialTracker tutorialTracker = new TutorialTracker();
    
    public GameState(Screen screen, MessageBus messageBus, String mapFile){
        this.screen = screen;
        this.messageBus = messageBus;
        map = new Map(messageBus);
        map.loadFromProperties(mapFile);
        notifications.push("You enter the dungeon...", Color.YELLOW);
    }
    
    /************************
     * Main messsage handler function
     */
    private void handleMessage(Message m){
        switch(m.getType()){
            case GameMessage.QUIT_TO_DESKTOP:
                terminate();
                break;
            case GameMessage.KEY_EVENT:
                Key key = (Key)m.getMsgObject();
                switch(key.getKind()){
                    case Escape:
                        if(isFocus()){
                            messageBus.enqueue(new Message(GameMessage.PUSH_INGAME_MENU_STATE));
                        }
                        break;
                }
                break;
            case GameMessage.RECEIVED_DAMAGE:
                onReceivedDamage((ReceivedDamageParams)m.getMsgObject());
                break;
            case GameMessage.TAMED:
                onTamed((TamedParams)m.getMsgObject());
                break;
            case GameMessage.TERMINATE_GAME_STATE:
                terminate();
                break;
            case GameMessage.PUSH_NOTIFICATION:
                onPushNotification((PushNotificationParams)m.getMsgObject());
                break;
            case GameMessage.PICKED_UP:
                onPickedUp((PickedUpParams)m.getMsgObject());
                break;
            case GameMessage.GAME_WON:
                notifications.push("You unlock the door and exit the dungeon!", Color.YELLOW);
                break;
            case GameMessage.GAME_LOST:
                notifications.push("You died!", Color.RED);
                break;
            case GameMessage.SAVE_GAME:
                map.storeToProperties((String)m.getMsgObject());
                break;                
        }
    }
    
    /***************************
     * Called when an item was picked up
     */
    private void onPickedUp(PickedUpParams pup){
        if(pup.pickupper == map.getPlayer()){
            int item_id = pup.pickedUpItem.getProto().id;
            if(
                       item_id == EntityFactory.ID_HEALTH_POTION
                    && !tutorialTracker.hasDone("PickedUpConsumable")
            ){
                tutorialTracker.setDone("PickedUpConsumable");
                messageBus.enqueue(new Message(GameMessage.PUSH_ALERT_STATE, 
                        "You picked up a consumable item!\n\n" +
                        "It has been added to your inventory in the lower right.\n" +
                        "Use the number keys to select the item, and then press F\n" + 
                        "to use it.\n\n" +
                        "Some consumables can also be used on other creatures.\n" +
                        "To do this, stand beside them, and use WASD to use the\n" +
                        "consumable on the creature."
                ));
            } else if (
                      (item_id == EntityFactory.ID_SWORD
                    || item_id == EntityFactory.ID_MAGIC_WAND)
                    && !tutorialTracker.hasDone("PickedUpWeapon")
            ){
                tutorialTracker.setDone("PickedUpWeapon");
                messageBus.enqueue(new Message(GameMessage.PUSH_ALERT_STATE, 
                        "You picked up a weapon!\n\n" + 
                        "It has been added to your inventory in the lower right.\n" + 
                        "Use the number keys to select the weapon, and then press\n" +
                        "any one of the WASD keys to use the weapon in the respective\n" +
                        "direction."
                ));
            }else if (
                    item_id == EntityFactory.ID_BOW
                    && !tutorialTracker.hasDone("PickedUpBow")
            ){
                tutorialTracker.setDone("PickedUpBow");
                messageBus.enqueue(new Message(GameMessage.PUSH_ALERT_STATE, 
                        "You picked up a bow!\n\n" + 
                        "It has been added to your inventory in the lower right.\n" + 
                        "Use the number keys to select the bow, and then press\n" +
                        "any one of the WASD keys to use the bow in the respective\n" +
                        "direction.\n\n" + 
                        "Before being able to shoot a bow, you will need some arrows.\n" +
                        "You should be able to find some around!"
                ));
            } else if (
                       item_id == EntityFactory.ID_TAMING_SCROLL
                    && !tutorialTracker.hasDone("PickedUpTamingScroll")
            ){
                tutorialTracker.setDone("PickedUpTamingScroll");
                messageBus.enqueue(new Message(GameMessage.PUSH_ALERT_STATE, 
                        "You picked up a taming scroll!\n\n" +
                        "It has been added to your inventory in the lower right.\n" + 
                        "Use the number keys to select the scroll, and then press\n" +
                        "any one of the WASD keys to use it in the respective\n" +
                        "direction.\n\n" +
                        "Upon using a taming scroll on a creature, you will attempt\n" + 
                        "to tame it. This process has a chance of failing.\n" +
                        "You can increase your taming chance by lowering the creatures\n" +
                        "HP until it is almost defeated.\n\n" + 
                        "If your taming attempt is successful, the creature will be added\n" +
                        "to your followers, its HP will be restored, and it will fight\n" + 
                        "for you in the future.\n\n" + 
                        "Tamed creatures are also stronger than normal ones (denoted by a +),\n" +
                        "so your follower should be able to win fights on his own!"
                ));
            } else if (
                       item_id == EntityFactory.ID_ARROW
                    && !tutorialTracker.hasDone("PickedUpArrow")
            ){
                tutorialTracker.setDone("PickedUpArrow");
                messageBus.enqueue(new Message(GameMessage.PUSH_ALERT_STATE, 
                        "You picked up an arrow!\n\n" + 
                        "You can now use a bow to shoot arrows at your foes.\n" +
                        "If you haven't found a bow yet, you should be able to\n" + 
                        "find one around."
                ));
            }
        }
    }
    
    /************************
     * Called when a taming attempt was made
     */
    private void onTamed(TamedParams tp){
        if(tp.tamerActor == map.getPlayer()){
            if(tp.success){
                notifications.push("You sucessfully tamed the " + tp.tamedActor.getName(), Color.GREEN);
            } else {
                notifications.push("You failed to tame the " + tp.tamedActor.getName(), Color.RED);
            }
        }
    }
    
    /************************
     * Called when something on the map received damage
     */
    private void onReceivedDamage(ReceivedDamageParams msg_obj){
        /********************************************
         * Check if the player was damaged
         */
        if(msg_obj.damagedActor == map.getPlayer()) {
            String notif_text;
            if(msg_obj.damagedActor.getHealth() <= 0){
                notif_text = String.format(
                        "The %s kills you.",
                        msg_obj.damagingActor.getName()
                );
            } else {
                 notif_text = String.format(
                        "You take %d damage from %s!",
                        msg_obj.damage,
                        msg_obj.damagingActor.getName()
                );
            }
            Color notif_color = msg_obj.damagingActor.getRepresent().getFGColor();
            notifications.push(notif_text, notif_color);
            return;
        }

        /********************************************
         * Check if an entity was damaged by the player directly
         */
        if(msg_obj.damagingActor == map.getPlayer()){
            String notif_text;
            if(msg_obj.damagedActor.getHealth() <= 0){
                notif_text = String.format(
                        "You kill the %s.", 
                        msg_obj.damagedActor.getName()
                );
            } else {
                notif_text = String.format(
                        "You inflict %d damage on %s! (%d/%d)",
                        msg_obj.damage,
                        msg_obj.damagedActor.getName(),
                        msg_obj.damagedActor.getHealth(),
                        msg_obj.damagedActor.getMaxHealth()
                );
            }
            Color notif_color = msg_obj.damagingActor.getRepresent().getFGColor();
            notifications.push(notif_text, notif_color);
            return;
        }

        /********************************************
         * Check if an entity was damaged by the player indirectly
         */
        EntityBase parent = msg_obj.damagingActor.getParent();
        while(parent != null){
            if(parent == map.getPlayer()){
                String notif_text;
                if(msg_obj.damagedActor.getHealth() <= 0){
                    notif_text = String.format(
                            "You kill the %s.", 
                            msg_obj.damagedActor.getName()
                    );
                } else {
                    notif_text = String.format(
                            "You inflict %d damage on %s! (%d/%d)",
                            msg_obj.damage,
                            msg_obj.damagedActor.getName(),
                            msg_obj.damagedActor.getHealth(),
                            msg_obj.damagedActor.getMaxHealth()
                    );
                }
                Color notif_color = parent.getRepresent().getFGColor();
                notifications.push(notif_text, notif_color);
                return;
            }
            parent = parent.getParent();
        }
        
        /********************************************
         * Check if an entity the player is leader of damaged something
         */
        Actor leader = msg_obj.damagingActor.getLeader();
        if(leader != null && leader == map.getPlayer()){
           
            String notif_text;
            if(msg_obj.damagedActor.getHealth() <= 0){
                notif_text = String.format(
                        "Your %s kills the %s.", 
                        msg_obj.damagingActor.getName(),
                        msg_obj.damagedActor.getName()
                );
            } else {
                notif_text = String.format(
                        "Your %s inflicts %d damage on %s! (%d/%d)",
                        msg_obj.damagingActor.getName(),
                        msg_obj.damage,
                        msg_obj.damagedActor.getName(),
                        msg_obj.damagedActor.getHealth(),
                        msg_obj.damagedActor.getMaxHealth()
                );
            }
            Color notif_color = msg_obj.damagingActor.getRepresent().getFGColor();
            notifications.push(notif_text, notif_color);
            return;
        }
    }

    private void onPushNotification(PushNotificationParams pnp){
        notifications.push(pnp.notification, pnp.color);
    }
    
    @Override
    public void onTick(double timeDelta) {
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
        
        drawFollowerList();

        Panel p_hud;
        Actor plr = map.getPlayer();
        p_hud = HudPanel.render(plr.getHealth(), plr.getMaxHealth(), plr.getInventory(), screen.width());
        screen.insert(p_hud, 0, screen.height() - 1);
    }
    
    @Override
    public void onTerminate(){
        tutorialTracker.store();
    }
    
    /**********************
     * Draws the list of creatures led by the player
     */
    private void drawFollowerList(){
        Actor player = map.getPlayer();
        
        List<Actor> follower_list = player.getFollowers();
        
        if(follower_list.size() > 0){
            Panel p_followers = FollowerPanel.render(follower_list);
            screen.insert(p_followers, screen.width() - p_followers.width(), NOTIFICATION_LIST_SIZE);
        }
    }
    
    /*********************
     * Generates the Rectangle for the are that will be drawn on the screen
     */
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
    
    /**********************
     * Draws the list of pickupables at the players location
     */
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
}
