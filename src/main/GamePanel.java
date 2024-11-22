package main;

import java.awt.AlphaComposite;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable {

	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	public static Board board = new Board();
	Mouse mouse = new Mouse();
	private CheckmateDetector checkmateDetector;
	CheckmateScreen checkmateScreen;

	// Piece
	public static ArrayList<Piece> simPieces = new ArrayList<>();

	public static ArrayList<Piece> whiteCapturedPieces = new ArrayList<>();
	public static ArrayList<Piece> blackCapturedPieces = new ArrayList<>();
	public static int whiteCapturedValue = 0;
	public static int blackCapturedValue = 0;
	Piece activePiece;

	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;

	public static int prevMoveStartCol = -1;
	public static int prevMoveStartRow = -1;
	public static int prevMoveTargetCol = -1;
	public static int prevMoveTargetRow = -1;

	public static int whiteKingCol, whiteKingRow;
	public static int blackKingCol, blackKingRow;

	private int hoveredCol, hoveredRow;

	public Board getBoard() {
		return board;
	}

	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		checkmateDetector = new CheckmateDetector(board, this);
		checkmateScreen = new CheckmateScreen(this);
		mouse.setCheckmateScreen(checkmateScreen);
		setPieces();
	}

	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	public void setPieces() {
		// White
		for (int i = 0; i < 8; i++) {
			simPieces.add(new Pawn(WHITE, i, 6));
		}
		simPieces.add(new Rook(WHITE, 0, 7));
		simPieces.add(new Rook(WHITE, 7, 7));
		simPieces.add(new Knight(WHITE, 1, 7));
		simPieces.add(new Knight(WHITE, 6, 7));
		simPieces.add(new Bishop(WHITE, 2, 7));
		simPieces.add(new Bishop(WHITE, 5, 7));
		simPieces.add(new Queen(WHITE, 3, 7));
		simPieces.add(new King(WHITE, 4, 7));

		// Black
		for (int i = 0; i < 8; i++) {
			simPieces.add(new Pawn(BLACK, i, 1));
		}
		simPieces.add(new Rook(BLACK, 0, 0));
		simPieces.add(new Rook(BLACK, 7, 0));
		simPieces.add(new Knight(BLACK, 1, 0));
		simPieces.add(new Knight(BLACK, 6, 0));
		simPieces.add(new Bishop(BLACK, 2, 0));
		simPieces.add(new Bishop(BLACK, 5, 0));
		simPieces.add(new Queen(BLACK, 3, 0));
		simPieces.add(new King(BLACK, 4, 0));

		// Add at the end of setPieces()
		for (Piece piece : simPieces) {
			if (piece instanceof King) {
				if (piece.color == WHITE) {
					whiteKingCol = piece.col;
					whiteKingRow = piece.row;
				} else {
					blackKingCol = piece.col;
					blackKingRow = piece.row;
				}
			}
		}

	}

	public void run() {
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		// Game loop
		while (gameThread != null) {

			currentTime = System.nanoTime();

			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;

			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}

		}

	}

	private void update() {
		if (checkmateScreen.isVisible()) {
			return;
		}

		if (mouse.pressed) {
			if (activePiece == null) {
				for (Piece piece : simPieces) {
					if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE
							&& piece.row == mouse.y / Board.SQUARE_SIZE) {

						activePiece = piece;

						activePiece.preCol = piece.col;
						activePiece.preRow = piece.row;

					}
				}
			} else {
				// Active piece is not null and mouse is pressed
				simulate();
			}
		} else {
			// Mouse released
			if (activePiece != null) {

				// The move is valid
				if (activePiece.hasValidMove(hoveredCol, hoveredRow)) {

					// Handling en passant capture
					if (activePiece instanceof Pawn) {
						if (Math.abs(hoveredCol - activePiece.col) == 1
								&& board.getPieceAt(hoveredCol, hoveredRow) == null) {
							Piece capturedPawn = board.getPieceAt(hoveredCol, hoveredRow + (1 - 2 * activePiece.color));

							if (capturedPawn.color == activePiece.color) {
								capturedPawn = null;
							} else {
								simPieces.remove(capturedPawn);
							}

							if (capturedPawn != null) {

								if (capturedPawn.color == WHITE) {
									whiteCapturedPieces.add(capturedPawn);
									whiteCapturedValue += 1;
								} else {
									blackCapturedPieces.add(capturedPawn);
									blackCapturedValue += 1;
								}

							}
						}
					}

					if (board.enemyPieceOnSquare(hoveredCol, hoveredRow, activePiece.color)) {
						Piece capturedPiece = board.getPieceAt(hoveredCol, hoveredRow);

						simPieces.remove(capturedPiece);

						if (capturedPiece.color == WHITE) {
							whiteCapturedPieces.add(capturedPiece);
							whiteCapturedValue += getPieceValue(capturedPiece);
						} else {
							blackCapturedPieces.add(capturedPiece);
							blackCapturedValue += getPieceValue(capturedPiece);
						}
					}

					// Handling castling
					if (activePiece instanceof King) {

						if (hoveredCol - activePiece.col == 2) {
							Piece castledRook = board.getPieceAt(7, activePiece.row);
							castledRook.col = 5;
							castledRook.x = castledRook.col * Board.SQUARE_SIZE;

						} else {

							if (hoveredCol - activePiece.col == -2) {
								Piece castledRook = board.getPieceAt(0, activePiece.row);
								castledRook.col = 3;
								castledRook.x = castledRook.col * Board.SQUARE_SIZE;
							}
						}
					}

					if ((activePiece instanceof Pawn && activePiece.color == 0 && hoveredRow == 0)
							|| (activePiece instanceof Pawn && activePiece.color == 1 && hoveredRow == 7)) {

						simPieces.add(new Queen(activePiece.color, hoveredCol, hoveredRow));
						simPieces.remove(activePiece);
					}

					prevMoveStartCol = activePiece.preCol;
					prevMoveStartRow = activePiece.preRow;
					prevMoveTargetCol = mouse.x / Board.SQUARE_SIZE;
					prevMoveTargetRow = mouse.y / Board.SQUARE_SIZE;

					activePiece.col = mouse.x / Board.SQUARE_SIZE;
					activePiece.row = mouse.y / Board.SQUARE_SIZE;

					activePiece.x = activePiece.col * Board.SQUARE_SIZE;
					activePiece.y = activePiece.row * Board.SQUARE_SIZE;

					if (activePiece instanceof King) {
						if (activePiece.color == WHITE) {
							whiteKingCol = activePiece.col;
							whiteKingRow = activePiece.row;
						} else {
							blackKingCol = activePiece.col;
							blackKingRow = activePiece.row;
						}
					}

					if (!activePiece.hasMoved()) {
						activePiece.setHasMoved(true);
					}

					activePiece = null;

					if (currentColor == WHITE) {
						currentColor = BLACK;
						if (checkmateDetector.isCheckmate(BLACK)) {
							checkmateScreen.show(WHITE);
							System.out.println("Checkmate! White wins!");
						}
					} else {
						currentColor = WHITE;
						if (checkmateDetector.isCheckmate(WHITE)) {
							checkmateScreen.show(BLACK);
							System.out.println("Checkmate! Black wins!");
						}
					}
				} else {
					// Move is against the rules
					activePiece.col = activePiece.preCol;
					activePiece.row = activePiece.preRow;

					activePiece.x = activePiece.getX(activePiece.preCol);
					activePiece.y = activePiece.getY(activePiece.preRow);

					activePiece = null;
				}
			}
		}
	}

	private void simulate() {

		// Updating current hovered square
		hoveredCol = mouse.x / Board.SQUARE_SIZE;
		hoveredRow = mouse.y / Board.SQUARE_SIZE;

		activePiece.x = mouse.x - (Board.HALF_SQUARE_SIZE);
		activePiece.y = mouse.y - (Board.HALF_SQUARE_SIZE);
	}

	private int getPieceValue(Piece piece) {
		if (piece instanceof Pawn)
			return 1;
		if (piece instanceof Knight)
			return 3;
		if (piece instanceof Bishop)
			return 3;
		if (piece instanceof Rook)
			return 5;
		if (piece instanceof Queen)
			return 9;
		return 0; // King or any other piece not captured
	}

	private void sortCapturedPieces(ArrayList<Piece> capturedPieces) {
		capturedPieces.sort((p1, p2) -> {
			int valueComparison = Integer.compare(getPieceValue(p1), getPieceValue(p2));
			if (valueComparison == 0) {
				// If values are equal, sort by piece type for consistent ordering
				return p1.getClass().getSimpleName().compareTo(p2.getClass().getSimpleName());
			}
			return valueComparison;
		});
	}

	private void drawCapturedPieces(Graphics2D g2) {
		int xOffset = (int) (Board.SQUARE_SIZE * 8.05);
		int yOffset = Board.SQUARE_SIZE * 3;

		sortCapturedPieces(whiteCapturedPieces);
		sortCapturedPieces(blackCapturedPieces);

		// Draw white captured pieces at the top right
		for (int i = 0; i < whiteCapturedPieces.size(); i++) {
			Piece p = whiteCapturedPieces.get(i);
			p.x = (int) (xOffset + (i % 8) * (Board.SQUARE_SIZE / 2.9)); // Horizontal spacing
			p.y = (int) (yOffset + (i / 8) * (Board.SQUARE_SIZE / 2.9)); // Vertical spacing

			int pieceWidth = Board.SQUARE_SIZE / 2; // Adjust this value to set the desired width
			int pieceHeight = Board.SQUARE_SIZE / 2; // Adjust this value to set the desired height
			g2.drawImage(p.image, p.x, p.y, pieceWidth, pieceHeight, null);
		}

		if (whiteCapturedValue > blackCapturedValue) {
			int valueDifferenceWhite = whiteCapturedValue - blackCapturedValue;
			String valueString = "+" + valueDifferenceWhite;
			int xPosition = xOffset;

			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(20f));
			g2.drawString(valueString, xPosition, yOffset - 10);
		}

		yOffset = HEIGHT / 2; // Set y position for black pieces
		for (int i = 0; i < blackCapturedPieces.size(); i++) {
			Piece p = blackCapturedPieces.get(i);
			p.x = (int) (xOffset + (i % 8) * (Board.SQUARE_SIZE / 2.9)); // Horizontal spacing
			p.y = (int) (yOffset + (i / 8) * (Board.SQUARE_SIZE / 2.9)); // Vertical spacing

			int pieceWidth = Board.SQUARE_SIZE / 2; // Adjust this value to set the desired width
			int pieceHeight = Board.SQUARE_SIZE / 2; // Adjust this value to set the desired height
			g2.drawImage(p.image, p.x, p.y, pieceWidth, pieceHeight, null);
		}

		int blackValueYOffset = blackCapturedPieces.size() > 8 ? 110 : 75;

		if (blackCapturedValue > whiteCapturedValue) {
			int valueDifferenceBlack = blackCapturedValue - whiteCapturedValue;
			String valueString = "+" + valueDifferenceBlack;
			int xPosition = xOffset;

			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(20f)); // Increase font size
			g2.drawString(valueString, xPosition, yOffset + blackValueYOffset);
		}
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		Color lightYellow = new Color(250, 245, 130);
		Color darkYellow = new Color(230, 210, 120);

		board.draw(g2);

		if (prevMoveStartCol != -1 && prevMoveStartRow != -1) {
			if ((prevMoveStartCol + prevMoveStartRow) % 2 == 0) {
				g2.setColor(lightYellow);
			} else {
				g2.setColor(darkYellow);
			}
			g2.fillRect(prevMoveStartCol * Board.SQUARE_SIZE, prevMoveStartRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
					Board.SQUARE_SIZE);
		}

		if (prevMoveTargetCol != -1 && prevMoveTargetRow != -1) {
			if ((prevMoveTargetCol + prevMoveTargetRow) % 2 == 0) {
				g2.setColor(lightYellow);
			} else {
				g2.setColor(darkYellow);
			}
			g2.fillRect(prevMoveTargetCol * Board.SQUARE_SIZE, prevMoveTargetRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
					Board.SQUARE_SIZE);
		}

		for (Piece p : simPieces) {
			p.draw(g2);
		}

		drawCapturedPieces(g2);

		if (activePiece != null) {
			g2.setColor(Color.white);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
			g2.fillRect(hoveredCol * Board.SQUARE_SIZE, hoveredRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
					Board.SQUARE_SIZE);
			g2.setColor(lightYellow);
			g2.fillRect(hoveredCol * Board.SQUARE_SIZE, hoveredRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
					Board.SQUARE_SIZE);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
					Board.SQUARE_SIZE);

			activePiece.draw(g2);
		}
		checkmateScreen.draw(g2);
	}

}