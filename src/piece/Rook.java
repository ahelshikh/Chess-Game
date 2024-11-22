package piece;

import main.GamePanel;

public class Rook extends Piece {
	
	public Rook(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_rook");
		} else {
			image = getImage("/piece/b_rook");
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
		
		if (this.isOnStraight(targetCol, targetRow) && GamePanel.board.isPathClear(preCol, preRow, targetCol, targetRow, color)) {
			return true;
		}
		
		return false;
	}
}
