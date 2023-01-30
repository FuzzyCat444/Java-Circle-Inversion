
package com.fuzzycat.circleinversion;

public class Selection {
    
    // Control point click radius
    public static final double RADIUS = 10.0;
    
    public static final int NONE = -1;
    public static final int CIRCLE = 0;
    
    public int type;
    public int index;
    public int point;

    public Selection(int type, int index, int point) {
        this.type = type;
        this.index = index;
        this.point = point;
    }
    
    public Selection() {
        this(NONE, 0, 0);
    }
}
