package main;

import java.util.ArrayList;

import piece.King;
import piece.Piece;
import piece.Queen;

public class CheckmateDetector {
    private Board board;
    private GamePanel gamePanel;
    
    public CheckmateDetector(Board board, GamePanel gamePanel) {
        this.board = board;
        this.gamePanel = gamePanel;
    }

    public boolean isCheckmate(int kingColor) {
        // First, check if the king is in check
        if (!board.isKingInCheck(kingColor)) {
            return false;
        }

        // Create a copy of the pieces list to avoid concurrent modification
        ArrayList<Piece> pieces = new ArrayList<>(GamePanel.simPieces);

        // Try all possible moves for all pieces of the same color
        for (Piece piece : pieces) {
            if (piece.color == kingColor) {
                // Store original position
                int originalCol = piece.col;
                int originalRow = piece.row;
                piece.preCol = originalCol;
                piece.preRow = originalRow;

                // Try all possible squares
                for (int targetRow = 0; targetRow < 8; targetRow++) {
                    for (int targetCol = 0; targetCol < 8; targetCol++) {
                        // Check if the move is valid
                        if (piece.hasValidMove(targetCol, targetRow)) {
                            // Store the piece at target location before move
                            Piece capturedPiece = board.getPieceAt(targetCol, targetRow);
                            
                            // Make temporary move
                            if (capturedPiece != null) {
                                GamePanel.simPieces.remove(capturedPiece);
                            }
                            
                            piece.col = targetCol;
                            piece.row = targetRow;
                            
                            // Update king position if moving king
                            if (piece instanceof King) {
                                if (piece.color == GamePanel.WHITE) {
                                    GamePanel.whiteKingCol = targetCol;
                                    GamePanel.whiteKingRow = targetRow;
                                } else {
                                    GamePanel.blackKingCol = targetCol;
                                    GamePanel.blackKingRow = targetRow;
                                }
                            }
                            
                            // Check if king is still in check after this move
                            boolean stillInCheck = board.isKingInCheck(kingColor);
                            
                            // Restore position
                            piece.col = originalCol;
                            piece.row = originalRow;
                            
                            // Restore king position if moving king
                            if (piece instanceof King) {
                                if (piece.color == GamePanel.WHITE) {
                                    GamePanel.whiteKingCol = originalCol;
                                    GamePanel.whiteKingRow = originalRow;
                                } else {
                                    GamePanel.blackKingCol = originalCol;
                                    GamePanel.blackKingRow = originalRow;
                                }
                            }
                            
                            // Restore captured piece if any
                            if (capturedPiece != null) {
                                GamePanel.simPieces.add(capturedPiece);
                            }
                            
                            // If we found a move that gets out of check, it's not checkmate
                            if (!stillInCheck) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        
        // If we get here, no moves were found to get out of check
        return true;
    }
}