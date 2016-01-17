package capstone2015.appstate;

import capstone2015.game.GameMessage;
import capstone2015.state.State;
import capstone2015.game.InputRecorder;
import capstone2015.graphics.Panel;
import capstone2015.graphics.Screen;
import capstone2015.graphics.TerminalChar;
import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

import java.awt.*;
import java.util.function.Consumer;

public class UserTextInputState extends State{

    public static int MAX_INPUT_LENGTH = 48;

    private Screen screen;
    private MessageBus messageBus;
    private Consumer<String> callbackFunc;
    private InputRecorder inputRecorder;

    public UserTextInputState(Screen screen, MessageBus messageBus, Consumer<String> callbackFunc){
        this.screen = screen;
        this.messageBus = messageBus;
        this.callbackFunc = callbackFunc;
        this.inputRecorder = new InputRecorder(messageBus, MAX_INPUT_LENGTH);
    }

    @Override
    public void onTick(double timeDelta) {
        for(Message m : messageBus){
            switch(m.getType()){
                case GameMessage.KEY_EVENT:
                    Key key = (Key)m.getMsgObject();
                    if(key.getKind() == Key.Kind.Enter)
                        onConfirmInput();
                    if(key.getKind() == Key.Kind.Escape)
                        onCancelInput();
                    break;
            }
        }

        inputRecorder.tick();

        int panel_width = MAX_INPUT_LENGTH + 2 + 2; //Max size of input + margin + border
        int panel_height = 7; //border + margin + 2 rows (text and help) + padding

        Panel p = Panel.borderPanel(panel_width, panel_height, Color.WHITE, Color.DARK_GRAY);

        p.insertCenterHorizontally(Panel.textPanel("Enter a Name for the Save"), 0);
        p.insertCenterHorizontally(Panel.fillPanel(MAX_INPUT_LENGTH, 1, new TerminalChar(' ', Color.WHITE, Color.BLACK)), 2);
        p.insertCenterHorizontally(Panel.textPanel(inputRecorder.getInput(), Color.WHITE, Color.BLACK), 2);
        p.insert(Panel.textPanel("Enter to Confirm, ESC to Cancel", Color.WHITE, Color.DARK_GRAY), 2, 4);

        screen.insertCenter(p);
    }

    private void onConfirmInput() {
        if(!inputRecorder.hasInput())
            return;

        callbackFunc.accept(inputRecorder.getInput());
        terminate();
    }

    private void onCancelInput() {
        callbackFunc.accept(null);
        terminate();
    }
}
