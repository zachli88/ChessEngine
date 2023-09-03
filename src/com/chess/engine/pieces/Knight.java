package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Knight extends Piece {
    private static final int[] CANDIDATE_MOVES = {-17, -15, -10, -6, 6, 10, 15, 17};
    public Knight(Alliance pieceAlliance, int position) {
        super(PieceType.KNIGHT, position, pieceAlliance, true);
    }
    public Knight(Alliance pieceAlliance, int position, boolean firstMove) {
        super(PieceType.ROOK, position, pieceAlliance, firstMove);
    }
    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getMovedPiece().pieceAlliance, move.getDestination());
    }

    @Override
    public List<Move> getLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int shift: CANDIDATE_MOVES) {
            int potentialMove = position + shift;
            if (isValidCoordinate(shift, potentialMove)) {
                Square candidateSquare = board.getSquare(potentialMove);
                if (!candidateSquare.occupied()) {
                    legalMoves.add(new MajorMove(board, this, potentialMove));
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
    private boolean isValidCoordinate(int shift, int potentialMove) {
        if (potentialMove < 0 || potentialMove >= Board.NUM_SQUARES) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 0 && (shift == -17 || shift == -10 ||
                shift == 6 || shift == 15)) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 1 && (shift == -10 || shift == 6)) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 6 && (shift == -6 || shift == 10)) {
            return false;
        }
        if (position % Board.NUM_SQUARES_PER_ROW == 7 && (shift == -15 || shift == -6 ||
                shift == 10 || shift == 17)) {
            return false;
        }
        return true;
    }
    public String toString() {
        return PieceType.KNIGHT.toString();
    }
}
