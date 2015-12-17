package capstone2015.appstate;

public abstract class AppState {    
    private boolean isAlive = true;
    private boolean isFocus = false;
    
    protected abstract void onTick(double timeDelta);
    protected abstract void onEvent(AppStateEvent event);
    
    public boolean isFocus(){return isFocus;}
    public boolean isBlur(){return !isFocus;}
    protected final void kill(){this.isAlive = false;}
    public final boolean isAlive(){return this.isAlive;}
    
    /*********
     * Sets the AppState blurred
     * meaning it is not at the top of the state stack anymore
     */
    public final void setBlur(){
        if(this.isFocus){
            this.isFocus = false;
            this.onEvent(AppStateEvent.BLUR);
        }
    }
    
    /*********
     * Sets the AppState focussed
     * meaning it is at the top of the state stack
     */
    public final void setFocus(){
        if(!this.isFocus){
            this.isFocus = true;
            this.onEvent(AppStateEvent.FOCUS);
        }
    }
}
