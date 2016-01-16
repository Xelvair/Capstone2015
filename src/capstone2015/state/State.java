package capstone2015.state;

public abstract class State {    
    private boolean isAlive = true;
    private boolean isFocus = false;
    
    protected abstract void onTick(double timeDelta);
    
    protected void  onBlur(){}
    protected void  onFocus(){}
    private void    onTerminate(){}
    
    public final boolean isFocus(){return isFocus;}
    public final boolean isBlur(){return !isFocus;}
    protected final void terminate(){this.isAlive = false; this.onTerminate();}
    public final boolean isAlive(){return this.isAlive;}
    
    /*********
     * Sets the State blurred
 meaning it is not at the top of the state stack anymore
     */
    public final void setBlur(){
        if(this.isFocus){
            this.isFocus = false;
            this.onBlur();
        }
    }
    
    /*********
     * Sets the State focussed
 meaning it is at the top of the state stack
     */
    public final void setFocus(){
        if(!this.isFocus){
            this.isFocus = true;
            this.onFocus();
        }
    }
}
