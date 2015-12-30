package capstone2015.entity;

/***************************
 * Models the types of solid bodies in the game
 * The full truth table for coolidesWith looks as follows:
 * clsn?    GHOST  FLUID   NORMAL  SOLID
 * GHOST    false   false   false   false
 * FLUID    false   false   false   true
 * NORMAL   false   false   true    true
 * SOLID    false   true    true    true
 */
public enum SolidType {
    GHOST,
    FLUID,
    NORMAL,
    SOLID;

    public static SolidType max(SolidType lhs, SolidType rhs){
        return (lhs.ordinal() < rhs.ordinal() ? rhs : lhs);
    }

    public static SolidType min(SolidType lhs, SolidType rhs){
        return (lhs.ordinal() < rhs.ordinal() ? lhs : rhs);
    }

    public boolean collidesWith(SolidType rhs){
        //Applying all the KNAWLEDGE from discrete mathematics
        return(
               ((this == NORMAL || this == SOLID) && (rhs == NORMAL || rhs == SOLID))
            || (this == FLUID && rhs == SOLID)
            || (this == SOLID && rhs == FLUID)
        );
    }
}
