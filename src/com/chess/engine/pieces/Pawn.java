package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {
    private final int[] CANDIDATE_MOVES = {8, 16, 7, 9};
    public Pawn(Alliance pieceAlliance, int position) {
        super(PieceType.PAWN, position, pieceAlliance, true);
    }
    public Pawn(Alliance pieceAlliance, int position, boolean firstMove) {
        super(PieceType.ROOK, position, pieceAlliance, firstMove);
    }
    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getMovedPiece().pieceAlliance, move.getDestination());
    }

    @Override
    public List<Move> getLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int shift : CANDIDATE_MOVES) {
            int potentialMove = position + shift * getPieceAlliance().getDirection();
            if (isValidCoordinate(shift, potentialMove, board)) {
                Square candidateSquare = board.getSquare(potentialMove);
                if (shift == 8) {
                    if (pieceAlliance.isPawnPromotionSquare(potentialMove)) {
                        legalMoves.add(new PawnPromotion(new PawnMove(board, this, potentialMove)));
                    }
                    else {
                        legalMoves.add(new PawnMove(board, this, potentialMove));
                    }
                }
                else if (shift == 16) {
                    legalMoves.add(new PawnJump(board, this, potentialMove));
                }
                else {
                    Piece otherPiece = candidateSquare.getPiece();
                    if (otherPiece != null) {
                        if (pieceAlliance != otherPiece.pieceAlliance) {
                            if (pieceAlliance.isPawnPromotionSquare(potentialMove)) {
                                legalMoves.add(new PawnPromotion(
                                        new PawnAttackMove(board, this, potentialMove, otherPiece)));
                            }
                            else {
                                legalMoves.add(new PawnAttackMove(board, this, potentialMove, otherPiece));
                            }
                        }
                    }
                    else {
                        Pawn enPassantPawn = board.getEnPassantPawn();
                        if (pieceAlliance != enPassantPawn.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassant(board, this, potentialMove, enPassantPawn));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    private boolean isValidCoordinate(int shift, int potentialMove, Board board) {
        if (potentialMove < 0 || potentialMove > 63) {
            return false;
        }
        if (shift == 8 || shift == 16) {
            if (board.getSquare(potentialMove).occupied()) {
                return false;
            }
        }
        if (shift == 16) {
            if (getPieceAlliance() == Alliance.WHITE) {
                if ((position > 55 || position < 48) ||
                        board.getSquare(potentialMove + 8).occupied()) {
                    return false;
                }
            }
            else if ((position > 15 || position < 8) ||
                        board.getSquare(potentialMove - 8).occupied()) {
                    return false;
            }
        }
        if (shift == 7 || shift == 9) {
            if (getPieceAlliance() == Alliance.WHITE) {
                if ((position % Board.NUM_SQUARES_PER_ROW == 0 && shift == 9) ||
                        (position % Board.NUM_SQUARES_PER_ROW == 7 && shift == 7)) {
                    return false;
                }
            }
            else if ((position % Board.NUM_SQUARES_PER_ROW == 0 && shift == 7) ||
                    (position % Board.NUM_SQUARES_PER_ROW == 7 && shift == 9)) {
                return false;
            }
            if (!board.getSquare(potentialMove).occupied()) {
                Pawn enPassantPawn = board.getEnPassantPawn();
                if (enPassantPawn != null && enPassantPawn.position == potentialMove
                        - 8 * pieceAlliance.getDirection()) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }
    public String toString() {
        return PieceType.PAWN.toString();
    }
    public Piece getPromotionPiece() {
        return new Queen(pieceAlliance, position, false);
    }
}
