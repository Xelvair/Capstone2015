package capstone2015.entity.states;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

/*******************************
 * Creates a signature of a state by combining the state class
 * and all its relevant parameters to a single hash.
 * 
 * The signature is able to instantiate an ActorState class from
 * its data.
 * 
 * If that hash changes, we know that the state of an actor
 * has changed, too.
 */
public class ActorStateSignature {
    private Class<?> stateClass;
    private Object[] params;
    
    public ActorStateSignature(Class<?> stateClass, Object... params){
        this.stateClass = stateClass;
        this.params = params;
    }
    
    public ActorState createInstance(){
        Class<?>[] parameter_types = new Class<?>[params.length];
        for(int i = 0; i < params.length; ++i){
            parameter_types[i] = params[i].getClass();
        }
        Constructor state_ctor;
        try {
            state_ctor = stateClass.getConstructor(parameter_types);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Failed to create class: no ctor found for parameters." + stateClass.getName() + params.toString());
        } catch (SecurityException ex) {
            throw new RuntimeException("Failed to create class: ctor is inaccessible.");
        }
        
        ActorState state; 
        try {
            state = (ActorState)state_ctor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to create class: failed to create instance." + ex.getClass().getName());
        }
        
        state.setHash(hashCode());
        state.setFocus();
        return state;
    }
    
    @Override
    public int hashCode(){
        return stateClass.hashCode() + Arrays.hashCode(params);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActorStateSignature other = (ActorStateSignature) obj;
        if (!Objects.equals(this.stateClass, other.stateClass)) {
            return false;
        }
        if (!Arrays.deepEquals(this.params, other.params)) {
            return false;
        }
        return true;
    }
}
