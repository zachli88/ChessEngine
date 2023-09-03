package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King extends Piece {
    private boolean firstMove;
    private static final int[] CANDIDATE_MOVES = {-9, -8, -7, -1, 1, 7, 8, 9};
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    private final boolean isCastled;
    public King(Alliance pieceAlliance, int position,
                boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, position, pieceAlliance, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    public King(Alliance pieceAlliance, int position, boolean firstMove,
                boolean isCastled, boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
        super(PieceType.KING, position, pieceAlliance, firstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }
    @Override
    public Piece movePiece(Move move) {
        return new King(move.getMovedPiece().pieceAlliance, move.getDestination(),
                false, move.isCastleMove(), false, false);
    }

    @Override
    public List<Move> getLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int shift: CANDIDATE_MOVES) {
            int potentialMove = position + shift;
            if (isValidCoordinate(shift, potentialMove)) {
                Square candidateSquare = board.getSquare(potentialMove);
                if (!candidateSquare.occupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, potentialMove));
                }
                else {
                    Piece otherPiece = candidateSquare.getPiece();
                    if (pieceAlliance != otherPiece.pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this, potentialMove, otherPiece));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    public boolean kingSideCastleCapable() {
        return kingSideCastleCapable;
    }
    public boolean queenSideCastleCapable() {
        return queenSideCastleCapable;
    }
    public boolean isCastled() {
        return isCastled;
    }
    private boolean isValidCoordinate(int shift, int potentialMove) {
        if (potentialMove < 0 || potentialMove >= Board.NUM_SQUARES) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 0 && (shift == -9 || shift == -1 ||
                shift == 7)) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 7 && (shift == -7 || shift == 1 ||
                shift == 9)) {
            return false;
        }
        return true;
    }
    public String toString() {
        return PieceType.KING.toString();
    }
}
