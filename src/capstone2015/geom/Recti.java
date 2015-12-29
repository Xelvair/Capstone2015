package capstone2015.geom;

public class Recti {
    private int left;
    private int top;
    private int width;
    private int height;
    
    public Recti(){
        this.left = 0;
        this.top = 0;
        this.width = 0;
        this.height = 0;
    }
    
    public Recti(int left, int top, int width, int height){
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
    
    public Recti(Recti rhs){
        this.left = rhs.left;
        this.top = rhs.top;
        this.width = rhs.width;
        this.height = rhs.height;
    }

    //////////////////////////////////
    // GETTERS
    //
    public int getLeft(){return this.left;}
    public int getTop(){return this.top;}
    public int getWidth(){return this.width;}
    public int getHeight(){return this.height;}
    
    //////////////////////////////////
    // SETTERS
    //
    public void setLeft(int left){this.left = left;}
    public void setTop(int top){this.top = top;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}
    public int getRight(){return (this.left + this.width) - 1;}
    public int getBottom(){return (this.top + this.height) - 1;}
    
    
    /**************************
     * Calculates the surface area of the rectangle
     * @return surface area of rect
     */
    public int getSurfaceArea(){
        return this.width * this.height;
    }    
    
    /*******************************
     * Checks whether a vector is inside the rectangle
     * @param vector vector to be checked
     * @return TRUE if rectangle contains vector, else false
     */
    public boolean contains(Vec2i vector){
        return (vector.getX() >= this.left &&
                vector.getY() >= this.top &&
                vector.getX() < this.left + this.width &&
                vector.getY() < this.top + this.height);
    }
    
    /*****************************
     * Checks whether the rectangle contains another rectangle
     * @param rect the rectangle that is potentially inside
     * @return TRUE if rect is inside this rectangle, else FALSE
     */
    public boolean contains(Recti rect){
        return(this.intersect(rect).equals(rect));
    }
    
    /*****************************
     * Translates this rectangle by a vector
     * @param translate the translation vector
     * @return the translated rect
     */
    public Recti translate(Vec2i translate){
        return new Recti(   this.left + translate.getX(), 
                            this.top + translate.getY(), 
                            this.width, this.height);
    }
    
    /****************************
     * Generates an intersection rectangle and returns it
     * @param rhs the other rect that this one will be
     * intersected with
     * @return the new rectangle, representing the intersection area
     * between the two rectangles
     */
    public Recti intersect(Recti rhs){
        Recti intersection = new Recti();
        
        intersection.left = Math.max(this.left, rhs.left);
        intersection.top = Math.max(this.top, rhs.top);
        
        int right = Math.min(this.left + this.width, rhs.left + rhs.width);
        int bottom = Math.min(this.top + this.height, rhs.top + rhs.height);
        
        intersection.width = Math.max(0, right - intersection.left);
        intersection.height = Math.max(0, bottom - intersection.top);
        
        if(intersection.width == 0 || intersection.height == 0){
            intersection = new Recti();
        }
        
        return intersection;
    }
    
    /******************************
     * Crops a rectangle
     * The cropped area is equivalent to the intersection
     * of this rectangle and a copy of this rectangle translated
     * by the crop vector
     * @param crop crop vector
     * @return the cropped rectangle
     */
    public Recti crop(Vec2i crop){
        return this.intersect(this.translate(crop));
    }
    
    /*********************************
     * Grows a rectangle by a factor
     * for example, growing a rectangle by (1, 1)
     * will make it 2 units wider and 2 units taller,
     * with the center remaining on the same spot
     * @param factor growth factor
     * @return the grown rectangle
     */
    public Recti grow(Vec2i factor){
        return new Recti(
                this.left - factor.getX(),
                this.top - factor.getY(),
                this.width + 2 * factor.getX(),
                this.height + 2 * factor.getY()
        );
    }
    
    /*******************************************************
     * Converts an absolute vector to a rectangle-relative vector
     * @param absolute absolute vector
     * @return relative vector
     */
    public Vec2i toRel(Vec2i absolute){
        return absolute.add(new Vec2i(-this.getLeft(), -this.getTop()));
    }
    
    /*******************************************************
     * Converts a rectangle-relative vector to an absolute vector
     * @param relative relative vector
     * @return absolute vector
     */
    public Vec2i toAbs(Vec2i relative){
        return relative.add(new Vec2i(this.getLeft(), this.getTop()));
    }
    
    /*******************************
     * Override for equals operator
     * @param rhs other rectangle to compare to
     * @return whether the rectangles equal
     */
    public boolean equals(Recti rhs){
        return (this.left == rhs.left &&
                this.top == rhs.top &&
                this.width == rhs.width &&
                this.height == rhs.height);
    }
    
    /******************************
     * Override for toString operator
     * @return stringified rectangle
     */
    @Override
    public String toString(){
        return String.format("{%d, %d, %d, %d}", this.left, this.top, this.width, this.height);
    }
}
