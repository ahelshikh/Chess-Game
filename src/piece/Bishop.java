package piece;

import main.Board;
import main.GamePanel;

public class Bishop extends Piece {
	
	public Bishop(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_bishop");
		} else {
			image = getImage("/piece/b_bishop");
		}
	}
	
	public boolean hasValidMove(int targetCol, int targetRow) {
		
		if (!this.isWithinBoard(targetCol, targetRow)) {
			return false;
		}
		
		if (!this.changedPosition(targetCol, targetRow)) {
			return false;
		}
		
		if (GamePanel.board.wouldMoveExposeCheck(this, targetCol, targetRow)) {
			return false;
		}
		
		if (this.isOnDiagonal(targetCol, targetRow) && GamePanel.board.isPathClear(preCol, preRow, targetCol, targetRow, color)) {
			return true;
		}
		
		return false;
	}
}
