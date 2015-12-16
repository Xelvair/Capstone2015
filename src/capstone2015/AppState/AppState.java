package capstone2015.AppState;

public abstract class AppState {    
    private boolean isAlive = true;
    private boolean isFocus = false;
    
    protected abstract void onTick(double timeDelta);
    protected abstract void onEvent(AppStateEvent event);
    
    public boolean isFocus(){return isFocus;}
    public boolean isBlur(){return !isFocus;}
    protected final void kill(){this.isAlive = false;}
    public final boolean isAlive(){return this.isAlive;}
    
    public final void setBlur(){
        if(this.isFocus){
            this.isFocus = false;
            this.onEvent(AppStateEvent.BLUR);
        }
    }
    
    public final void setFocus(){
        if(!this.isFocus){
            this.isFocus = true;
            this.onEvent(AppStateEvent.FOCUS);
        }
    }
}
