
package com.fuzzycat.circleinversion;

import java.awt.Color;
import java.awt.Graphics2D;

public class Circle implements Drawable {

    // Radius of visible control points
    private static final double POINT_RADIUS = 3.0;
    
    // dx, dy = location of circumference control point relative to x, y
    public double x, y, dx, dy;
    
    public Circle(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
    
    public Circle() {
        this(0.0, 0.0, 0.0, 0.0);
    }
    
    public double radius() {
        return Math.sqrt(this.dx * this.dx + this.dy * this.dy);
    }
    
    public double radiusSquared() {
        return this.dx * this.dx + this.dy * this.dy;
    }
    
    public double[] invertPoint(double x, double y) {
        // Location of x, y with respect to circle's center x, y
        double xoff = x - this.x;
        double yoff = y - this.y;
        /* Implementation of OP * OP' = R^2 where O is the center of the circle,
           P is the original point inside/outside the circle, and P' is the
           point P inverted with respect to the circle. */
        double r1_2 = xoff * xoff + yoff * yoff;
        double ir1_2 = 1.0 / r1_2;
        double r_2 = radiusSquared();
        // Scale the xoff, yoff vector to be the length of OP'
        double xret = this.x + r_2 * xoff * ir1_2;
        double yret = this.y + r_2 * yoff * ir1_2;
        return new double[] { xret, yret };
    }
    
    // Is mouse at center control point
    public boolean atCenter(double x, double y) {
        double xoff = x - this.x;
        double yoff = y - this.y;
        double sr = Selection.RADIUS;
        return xoff * xoff + yoff * yoff < sr * sr;
    }
    
    // Is mouse at circumference control point
    public boolean atCircumference(double x, double y) {
        double xoff = x - (this.x + this.dx);
        double yoff = y - (this.y + this.dy);
        double sr = Selection.RADIUS;
        return xoff * xoff + yoff * yoff < sr * sr;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        double radius = radius();
        int iradius = (int) radius;
        int width = (int) (2.0 * radius);
        g2d.setColor(Color.BLACK);
        // Draw circle
        g2d.drawOval((int) x - iradius, (int) y - iradius, width, width);
    
        g2d.setColor(Color.RED);
        // Draw control points
        g2d.fillOval((int) (this.x - POINT_RADIUS), 
                     (int) (this.y - POINT_RADIUS), 
                     (int) (POINT_RADIUS * 2.0), 
                     (int) (POINT_RADIUS * 2.0));
        g2d.fillOval((int) (this.x + this.dx - POINT_RADIUS), 
                     (int) (this.y + this.dy - POINT_RADIUS), 
                     (int) (POINT_RADIUS * 2.0), 
                     (int) (POINT_RADIUS * 2.0));
    }
}
