package piece;

import main.GamePanel;

public class Knight extends Piece {
	
	public Knight(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_knight");
		} else {
			image = getImage("/piece/b_knight");
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
		
		int colChange = Math.abs(targetCol - preCol);
		int rowChange = Math.abs(targetRow - preRow);

		if ((colChange == 2 && rowChange == 1) || (colChange == 1 && rowChange == 2)) {
			return !GamePanel.board.friendlyPieceOnSquare(targetCol, targetRow, color);
		}

		return false;
	}
}
