package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class Board {

	final int MAX_COL = 8;
	final int MAX_ROW = 8;
	public static final int SQUARE_SIZE = 100;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

	public void draw(Graphics2D g2) {
		int c = 1;
		for (int row = 0; row < MAX_ROW; row++) {
			if (c == 0) {
				c = 1;
			} else {
				c = 0;
			}
			for (int col = 0; col < MAX_COL; col++) {
				if (c == 0) {
					g2.setColor(new Color(240, 215, 175));
					c = 1;
				} else {
					g2.setColor(new Color(185, 135, 98));
					c = 0;
				}
				g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
	}

	public Piece getPieceAt(int col, int row) {
		for (Piece piece : GamePanel.simPieces) {
			if (piece.col == col && piece.row == row) {
				return piece;
			}
		}
		return null;
	}

	public boolean friendlyPieceOnSquare(int targetCol, int targetRow, int pieceColor) {
		Piece targetPiece = getPieceAt(targetCol, targetRow);
		if (targetPiece != null && targetPiece.color == pieceColor) {
			return true;
		}
		return false;
	}

	public boolean enemyPieceOnSquare(int targetCol, int targetRow, int pieceColor) {
		Piece targetPiece = getPieceAt(targetCol, targetRow);
		if (targetPiece != null && targetPiece.color != pieceColor) {
			return true;
		}
		return false;
	}

	public boolean isPathClear(int startCol, int startRow, int targetCol, int targetRow, int pieceColor) {
		int colDirection = (int) Math.signum(targetCol - startCol);
		int rowDirection = (int) Math.signum(targetRow - startRow);

		int currentCol = startCol + colDirection;
		int currentRow = startRow + rowDirection;

		while (currentCol != targetCol || currentRow != targetRow) {
			if (getPieceAt(currentCol, currentRow) != null) {
				return false;
			}
			currentCol += colDirection;
			currentRow += rowDirection;
		}

		if (friendlyPieceOnSquare(targetCol, targetRow, pieceColor)) {
			return false;
		}

		return true;
	}

	public boolean pathClearForCastling(String castleType, int kingRow) {
		if (castleType.equals("SHORT")) {
			return getPieceAt(5, kingRow) == null && getPieceAt(6, kingRow) == null;
		} else {
			return getPieceAt(3, kingRow) == null && getPieceAt(2, kingRow) == null && getPieceAt(1, kingRow) == null;
		}
	}

	public boolean pieceInFront(int startCol, int startRow, int pieceColor) {
		int direction;
		if (pieceColor == 0) {
			direction = -1;
		} else {
			direction = 1;
		}
		Piece pieceInFront = getPieceAt(startCol, startRow + direction);
		return !(pieceInFront == null);
	}

	private List<Piece> getAllPieces(int color) {
		List<Piece> pieces = new ArrayList<>();
		for (Piece piece : GamePanel.simPieces) {
			if (piece.color == color) {
				pieces.add(piece);
			}
		}
		return pieces;
	}

	public boolean isSquareUnderAttack(int row, int col, int kingColor) {
		ArrayList<Piece> pieces = new ArrayList<>(GamePanel.simPieces);

		for (Piece piece : pieces) {
			if (piece.color != kingColor) {
				int originalCol = piece.col;
				int originalRow = piece.row;

				piece.preCol = piece.col;
				piece.preRow = piece.row;

				if (piece instanceof Pawn) {
					int direction = (piece.color == GamePanel.WHITE) ? -1 : 1;
					if (Math.abs(col - piece.col) == 1 && (row - piece.row) == direction) {
						return true;
					}
				} else {
					if (canPieceAttackSquare(piece, col, row)) {
						return true;
					}
				}

				piece.preCol = originalCol;
				piece.preRow = originalRow;
			}
		}
		return false;
	}

	private boolean canPieceAttackSquare(Piece piece, int col, int row) {
		if (!piece.isWithinBoard(col, row) || !piece.changedPosition(col, row)) {
			return false;
		}

		if (piece instanceof King) {
			int colChange = Math.abs(col - piece.col);
			int rowChange = Math.abs(row - piece.row);
			return colChange <= 1 && rowChange <= 1;
		}

		if (piece instanceof Knight) {
			int colChange = Math.abs(col - piece.col);
			int rowChange = Math.abs(row - piece.row);
			return (colChange == 2 && rowChange == 1) || (colChange == 1 && rowChange == 2);
		}

		// The key changes are here:
		if (piece instanceof Bishop) {
			return piece.isOnDiagonal(col, row) && isPathClear(piece.col, piece.row, col, row, piece.color);
		}

		if (piece instanceof Rook) {
			return piece.isOnStraight(col, row) && isPathClear(piece.col, piece.row, col, row, piece.color);
		}

		if (piece instanceof Queen) {
			return (piece.isOnDiagonal(col, row) || piece.isOnStraight(col, row)) 
				&& isPathClear(piece.col, piece.row, col, row, piece.color);
		}

		return false;
	}

	public boolean isKingInCheck(int kingColor) {
		int kingRow = (kingColor == GamePanel.WHITE) ? GamePanel.whiteKingRow : GamePanel.blackKingRow;
		int kingCol = (kingColor == GamePanel.WHITE) ? GamePanel.whiteKingCol : GamePanel.blackKingCol;
		return isSquareUnderAttack(kingRow, kingCol, kingColor);
	}

	public boolean wouldMoveExposeCheck(Piece piece, int targetCol, int targetRow) {
		int originalCol = piece.col;
		int originalRow = piece.row;

		Piece capturedPiece = getPieceAt(targetCol, targetRow);
		boolean pieceWasCaptured = false;

		if (capturedPiece != null) {
			GamePanel.simPieces.remove(capturedPiece);
			pieceWasCaptured = true;
		}

		piece.col = targetCol;
		piece.row = targetRow;

		if (piece instanceof King) {
			if (piece.color == GamePanel.WHITE) {
				GamePanel.whiteKingCol = targetCol;
				GamePanel.whiteKingRow = targetRow;
			} else {
				GamePanel.blackKingCol = targetCol;
				GamePanel.blackKingRow = targetRow;
			}
		}

		boolean wouldBeInCheck = isKingInCheck(piece.color);

		piece.col = originalCol;
		piece.row = originalRow;

		if (piece instanceof King) {
			if (piece.color == GamePanel.WHITE) {
				GamePanel.whiteKingCol = originalCol;
				GamePanel.whiteKingRow = originalRow;
			} else {
				GamePanel.blackKingCol = originalCol;
				GamePanel.blackKingRow = originalRow;
			}
		}

		if (pieceWasCaptured) {
			GamePanel.simPieces.add(capturedPiece);
		}

		return wouldBeInCheck;
	}
}