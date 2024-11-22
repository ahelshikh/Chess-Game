package piece;

import java.awt.Graphics2D;

import main.Board;
import main.GamePanel;

public class Pawn extends Piece {

	private double scale = 0.8;

	public Pawn(int color, int col, int row) {
		super(color, col, row);

		if (color == GamePanel.WHITE) {
			image = getImage("/piece/w_pawn");
		} else {
			image = getImage("/piece/b_pawn");
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
		
		int colChange = targetCol - preCol;
		int rowChange = targetRow - preRow;

		boolean enemyPieceOnSquare = GamePanel.board.enemyPieceOnSquare(targetCol, targetRow, this.color);

		// WHITE
		if (this.color == GamePanel.WHITE) {
			// Diagonal Capture
			if (Math.abs(colChange) == 1 && rowChange == -1) {
				if (enemyPieceOnSquare) {
					return true;
				} else {
					return (GamePanel.prevMoveTargetRow == targetRow + 1
							&& (GamePanel.prevMoveTargetRow - GamePanel.prevMoveStartRow) == 2
							&& GamePanel.board.getPieceAt(targetCol, targetRow + 1) instanceof Pawn);
				}
			}

			// Moving forward
			if (!this.hasMoved) {
				// Pawn can move forward twice on first move
				return colChange == 0 && (rowChange == -1 || rowChange == -2)
						&& !GamePanel.board.pieceInFront(preCol, preRow, GamePanel.WHITE);
			} else {
				return colChange == 0 && rowChange == -1 && !enemyPieceOnSquare;
			}
		} else {
			// BLACK
			if (Math.abs(colChange) == 1 && rowChange == 1) {
				if (enemyPieceOnSquare) {
					return true;
				} else {
					return (GamePanel.prevMoveTargetRow == targetRow - 1
							&& (GamePanel.prevMoveTargetRow - GamePanel.prevMoveStartRow) == -2
							&& GamePanel.board.getPieceAt(targetCol, targetRow - 1) instanceof Pawn);
				}
			}

			// Moving forward
			if (!this.hasMoved) {
				// Pawn can move forward twice on first move
				return colChange == 0 && (rowChange == 1 || rowChange == 2)
						&& !GamePanel.board.pieceInFront(preCol, preRow, GamePanel.BLACK);
			} else {
				return colChange == 0 && rowChange == 1 && !enemyPieceOnSquare;
			}
		}
	}

	public void draw(Graphics2D g2) {

		int width = (int) (Board.SQUARE_SIZE * scale);
		int height = (int) (Board.SQUARE_SIZE * scale);

		int xOffset = (Board.SQUARE_SIZE - width) / 2;
		int yOffset = (Board.SQUARE_SIZE - height) / 2;
		g2.drawImage(image, x + xOffset, y + yOffset, width, height, null);

	}
}
