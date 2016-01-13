package capstone2015.messaging;

import capstone2015.entity.Actor;
import java.util.function.Consumer;

public class AttemptTameParams {
    public Actor tamerActor;
    public Actor tamedActor;
    public Consumer<Boolean> tameCallback;
}
