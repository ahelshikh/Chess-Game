package piece;

import main.GamePanel;

public class Queen extends Piece {
	
	public Queen(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_queen");
		} else {
			image = getImage("/piece/b_queen");
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
		
		if ((this.isOnDiagonal(targetCol, targetRow) || this.isOnStraight(targetCol, targetRow)) && GamePanel.board.isPathClear(preCol, preRow, targetCol, targetRow, color)) {
			return true;
		}
		
		return false;
	}
}
