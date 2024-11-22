package piece;

import main.GamePanel;

public class King extends Piece {

	public King(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_king");
		} else {
			image = getImage("/piece/b_king");
		}
	}

	public boolean hasValidMove(int targetCol, int targetRow) {

		if (!this.isWithinBoard(targetCol, targetRow)) {
			return false;
		}

		if (!this.changedPosition(targetCol, targetRow)) {
			return false;
		}

		if (GamePanel.board.isSquareUnderAttack(targetRow, targetCol, color)) {
			return false;
		}

		int colChange = Math.abs(targetCol - preCol);
		int rowChange = Math.abs(targetRow - preRow);

		int colDirection = (int) Math.signum(targetCol - preCol);

		if (colChange <= 1 && rowChange <= 1) {
			return !GamePanel.board.friendlyPieceOnSquare(targetCol, targetRow, color);
		}

		// Castling
		if (!hasMoved && colChange == 2 && rowChange == 0) {
			if (colDirection == 1) {
				// Short castle
				if (GamePanel.board.pathClearForCastling("SHORT", preRow)
						&& !GamePanel.board.getPieceAt(7, preRow).hasMoved) {
					return true;
				}
			} else {
				// Long castle
				if (GamePanel.board.pathClearForCastling("LONG", preRow)
						&& !GamePanel.board.getPieceAt(0, preRow).hasMoved) {
					return true;
				}
			}
		}

		return false;
	}
}
