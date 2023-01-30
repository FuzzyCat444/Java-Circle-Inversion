
package com.fuzzycat.circleinversion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class CircleInversion extends JPanel 
        implements Runnable, MouseInputListener, KeyListener {
    
    private BufferedImage screen;
    private Graphics2D g2d;
    
    private List<Circle> circles;
    private Selection selection;
    
    public CircleInversion() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        
        screen = new BufferedImage(1, 1,
            BufferedImage.TYPE_INT_RGB);
        g2d = screen.createGraphics();
        
        circles = new ArrayList<>();
        selection = new Selection();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (screen.getWidth() != getWidth() || 
            screen.getHeight() != getHeight()) {
            
            screen = new BufferedImage(getWidth(), getHeight(), 
                    BufferedImage.TYPE_INT_RGB);
            g2d = screen.createGraphics();
        }
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        int[] rgb = ((DataBufferInt) screen.getRaster().getDataBuffer()).getData();
        int pi = 0;
        for (int y = 0; y < screen.getHeight(); y++) {
            for (int x = 0; x < screen.getWidth(); x++) {
                double sx = x + 0.5;
                double sy = y + 0.5;
                /* Invert every pixel location with respect to every circle.
                   All points outside the circle in the infinite plane will be
                   mapped to the inside of the circle, and vice versa. */
                for (int i = circles.size() - 1; i >= 0; i--) {
                    Circle circle = circles.get(i);
                    double[] inv = circle.invertPoint(sx, sy);
                    sx = inv[0];
                    sy = inv[1];
                }
                int sxi = (int) sx;
                int syi = (int) sy;
                /* Quickly divide (using >>) x and y coordinates by 64 and 128 
                   and do a quick mod 2 operation (using &) to create a
                   checkerboard pattern. */
                int sum1 = (sxi >> 6) + (syi >> 6);
                int sum2 = (sxi >> 7) + (syi >> 7);
                if ((sum2 & 1) == 0) {
                    if ((sum1 & 1) == 0) {
                        rgb[pi] = 0xffff00;
                    } else {
                        rgb[pi] = 0x0000ff;
                    }
                } else {
                    if ((sum1 & 1) == 0) {
                        rgb[pi] = 0xff00ff;
                    } else {
                        rgb[pi] = 0x00ffff;
                    }
                }
                pi++;
            }
        }
        
        // Draw editable circles and their control points
        for (int i = 0; i < circles.size(); i++) {
            Circle circle = circles.get(i);
            circle.draw(g2d);
        }
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(screen, 0, 0, null);
    }
    
    @Override
    public void run() {
        while (true) {
            requestFocus();
            repaint();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CircleInversion circleInversion = new CircleInversion();
                new Thread(circleInversion).start();
                JFrame frame = new JFrame("FuzzyCat: Circle Inversion");
                frame.add(circleInversion);
                frame.setSize(800, 600);
                frame.setResizable(true);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            double r = getWidth() * 0.075;
            circles.add(new Circle(e.getX(), e.getY(), r, 0.0));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        selection = new Selection();
        for (int i = 0; i < circles.size(); i++) {
            Circle circle = circles.get(i);
            if (circle.atCircumference(e.getX(), e.getY())) {
                selection.type = Selection.CIRCLE;
                selection.index = i;
                selection.point = 1;
                return;
            } else if (circle.atCenter(e.getX(), e.getY())) {
                selection.type = Selection.CIRCLE;
                selection.index = i;
                selection.point = 0;
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selection = new Selection();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selection.type == Selection.CIRCLE) {
            Circle circle = circles.get(selection.index);
            if (selection.point == 0) {
                circle.x = e.getX();
                circle.y = e.getY();
            } else if (selection.point == 1) {
                circle.dx = e.getX() - circle.x;
                circle.dy = e.getY() - circle.y;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X) {
            if (selection.type == Selection.CIRCLE) {
                circles.remove(selection.index);
            }
            selection = new Selection();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
