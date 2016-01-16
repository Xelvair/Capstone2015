package capstone2015.game;

import capstone2015.messaging.Message;
import capstone2015.messaging.MessageBus;
import com.googlecode.lanterna.input.Key;

public class InputRecorder {
    private MessageBus messageBus;
    private String input = "";
    private int maxInputLength;

    public InputRecorder(MessageBus messageBus, int maxInputLength){
        this.messageBus = messageBus;
        this.maxInputLength = maxInputLength;
    }

    public void tick(){
        for(Message m : messageBus){
            if(m.getType() == GameMessage.KEY_EVENT){
                Key key = (Key)m.getMsgObject();

                switch(key.getKind()){
                    case Backspace:
                        if(input.length() > 0)
                            input = input.substring(0, input.length() - 1);
                        break;
                    case NormalKey:
                        if(input.length() < maxInputLength)
                            input += key.getCharacter();
                        break;
                }
            }
        }
    }

    public String getInput(){
        return input;
    }

    public boolean hasInput(){
        return input.length() > 0;
    }
}
