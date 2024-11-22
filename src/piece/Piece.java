package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;

public class Piece {

	public BufferedImage image;
	public int x, y;
	public int col, row, preCol, preRow;
	public int color;
	public boolean hasMoved = false;

	public Piece(int color, int col, int row) {

		this.color = color;
		this.col = col;
		this.row = row;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}

	public BufferedImage getImage(String imagePath) {
		BufferedImage image = null;

		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public int getX(int col) {
		return col * Board.SQUARE_SIZE;
	}

	public int getY(int row) {
		return row * Board.SQUARE_SIZE;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean moved) {
		hasMoved = moved;
	}
	public boolean changedPosition(int targetCol, int targetRow) {
		
		if ((preCol == targetCol) && (preRow == targetRow)) {
			return false;
		}
		
		return true;
	}
	public boolean isOnDiagonal(int targetCol, int targetRow) {
		
		int colChange = Math.abs(targetCol - preCol);
		int rowChange = Math.abs(targetRow - preRow);
		
		if (colChange == rowChange) {
			return true;
		}
		
		return false;
	}
	
	public boolean isOnStraight(int targetCol, int targetRow) {
		
		if ((targetCol == preCol) || (targetRow == preRow)) {
			return true;
		}
		
		return false;
		
	}
	
	public boolean isWithinBoard(int col, int row) {
	    return col >= 0 && col <= 7 && row >= 0 && row <= 7;
	}


	public boolean hasValidMove(int targetCol, int targetRow) {
		return false;
	}
	
	public boolean isPathClear(int targetCol, int targetRow) {
		return true;
	}

	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);

	}

}
