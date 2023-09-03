package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Bishop extends Piece{
    private final int[] CANDIDATE_MOVES = {-9, -7, 7, 9};

    public Bishop(Alliance pieceAlliance, int position) {
        super(PieceType.BISHOP, position, pieceAlliance, true);
    }
    public Bishop(Alliance pieceAlliance, int position, boolean firstMove) {
        super(PieceType.ROOK, position, pieceAlliance, firstMove);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Bishop(move.getMovedPiece().pieceAlliance, move.getDestination());
    }

    @Override
    public List<Move> getLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int shift: CANDIDATE_MOVES) {
            int newPosition = position;
            int potentialMove = position + shift;
            while (isValidCoordinate(shift, potentialMove, newPosition)) {
                Square candidateSquare = board.getSquare(potentialMove);
                if (!candidateSquare.occupied()) {
                    legalMoves.add(new MajorMove(board, this, potentialMove));
                    potentialMove += shift;
                    newPosition += shift;
                }
                else {
                    Piece otherPiece = candidateSquare.getPiece();
                    if (pieceAlliance != otherPiece.pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this, potentialMove, otherPiece));
                    }
                    break;
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }
    public String toString() {
        return PieceType.BISHOP.toString();
    }
    private boolean isValidCoordinate(int shift, int potentialMove, int newPosition) {
        if (potentialMove < 0 || potentialMove >= Board.NUM_SQUARES) {
            return false;
        }
        if (newPosition % Board.NUM_SQUARES_PER_ROW == 0 && (shift == -9 || shift == 7)) {
            return false;
        }
        if (newPosition % Board.NUM_SQUARES_PER_ROW == 7 && (shift == -7 || shift == 9)) {
            return false;
        }
        return true;
    }
}
