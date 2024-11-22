package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.AlphaComposite;

public class CheckmateScreen {
    private Rectangle restartButton;
    private boolean isVisible;
    private boolean isButtonHovered;
    private String winner;
    private GamePanel gamePanel;
    
    // Colors for different button states
    private final Color BUTTON_COLOR = new Color(50, 120, 200);
    private final Color BUTTON_HOVER_COLOR = new Color(70, 140, 220);
    
    public CheckmateScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.isVisible = false;
        this.isButtonHovered = false;
        // Create button in the center of the screen
        int buttonWidth = 200;
        int buttonHeight = 50;
        restartButton = new Rectangle(
            (GamePanel.WIDTH - buttonWidth) / 2,
            (GamePanel.HEIGHT - buttonHeight) / 2 + 50,
            buttonWidth,
            buttonHeight
        );
    }
    
    public void show(int winnerColor) {
        isVisible = true;
        winner = winnerColor == GamePanel.WHITE ? "White" : "Black";
    }
    
    public void hide() {
        isVisible = false;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void handleClick(int x, int y) {
        if (isVisible && restartButton.contains(x, y)) {
            restartGame();
        }
    }
    
    public void handleMouseMove(int x, int y) {
        isButtonHovered = restartButton.contains(x, y);
    }
    
    private void restartGame() {
        // Reset all game state
        GamePanel.simPieces.clear();
        GamePanel.whiteCapturedPieces.clear();
        GamePanel.blackCapturedPieces.clear();
        GamePanel.whiteCapturedValue = 0;
        GamePanel.blackCapturedValue = 0;
        GamePanel.prevMoveStartCol = -1;
        GamePanel.prevMoveStartRow = -1;
        GamePanel.prevMoveTargetCol = -1;
        GamePanel.prevMoveTargetRow = -1;
        
        // Reset the board
        gamePanel.setPieces();
        gamePanel.currentColor = GamePanel.WHITE;
        
        // Hide the checkmate screen
        hide();
    }
    
    public void draw(Graphics2D g2) {
        if (!isVisible) return;
        
        // Draw semi-transparent black overlay
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw "Checkmate!" text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        String checkmateText = "Checkmate!";
        FontMetrics metrics = g2.getFontMetrics();
        int x = (GamePanel.WIDTH - metrics.stringWidth(checkmateText)) / 2;
        g2.drawString(checkmateText, x, GamePanel.HEIGHT / 2 - 50);
        
        // Draw winner text
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        String winnerText = winner + " Wins!";
        metrics = g2.getFontMetrics();
        x = (GamePanel.WIDTH - metrics.stringWidth(winnerText)) / 2;
        g2.drawString(winnerText, x, GamePanel.HEIGHT / 2);
        
        // Draw restart button with hover effect
        g2.setColor(isButtonHovered ? BUTTON_HOVER_COLOR : BUTTON_COLOR);
        g2.fill(restartButton);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.draw(restartButton);
        
        // Draw button text
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String buttonText = "Restart Game";
        metrics = g2.getFontMetrics();
        x = restartButton.x + (restartButton.width - metrics.stringWidth(buttonText)) / 2;
        int y = restartButton.y + (restartButton.height - metrics.getHeight()) / 2 + metrics.getAscent();
        g2.drawString(buttonText, x, y);
    }
}