package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter { 
    
    public int x, y;
    public boolean pressed;
    private CheckmateScreen checkmateScreen;
    
    public void setCheckmateScreen(CheckmateScreen checkmateScreen) {
        this.checkmateScreen = checkmateScreen;
    }
    
    public void mousePressed(MouseEvent e) {
        pressed = true;
        
        // Handle checkmate screen button clicks
        if (checkmateScreen != null && checkmateScreen.isVisible()) {
            checkmateScreen.handleClick(e.getX(), e.getY());
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        updateCheckmateScreenHover(e);
    }
    
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        updateCheckmateScreenHover(e);
    }
    
    private void updateCheckmateScreenHover(MouseEvent e) {
        if (checkmateScreen != null && checkmateScreen.isVisible()) {
            checkmateScreen.handleMouseMove(e.getX(), e.getY());
        }
    }
}