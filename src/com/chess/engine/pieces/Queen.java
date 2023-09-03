package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Queen extends Piece {
    public Queen(Alliance pieceAlliance, int position) {
        super(PieceType.QUEEN, position, pieceAlliance, true);
    }
    public Queen(Alliance pieceAlliance, int position, boolean firstMove) {
        super(PieceType.ROOK, position, pieceAlliance, firstMove);
    }
    @Override
    public Piece movePiece(Move move) {
        return new Queen(move.getMovedPiece().pieceAlliance, move.getDestination());
    }
    private final int[] CANDIDATE_MOVES = {-9, -8, -7, -1, 1, 7, 8, 9};
    @Override
    public List<Move> getLegalMoves(Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int shift: CANDIDATE_MOVES) {
            int potentialMove = position + shift;
            int newPosition = position;
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

    private boolean isValidCoordinate(int shift, int potentialMove, int newPosition) {
        if (potentialMove < 0 || potentialMove >= Board.NUM_SQUARES) {
            return false;
        }
        if (newPosition % Board.NUM_SQUARES_PER_ROW == 0 &&
                (shift == -9 || shift == 7 || shift == -1)) {
            return false;
        }
        if (newPosition % Board.NUM_SQUARES_PER_ROW == 7 &&
                (shift == -7 || shift == 9 || shift == 1)) {
            return false;
        }
        return true;
    }
    public String toString() {
        return PieceType.QUEEN.toString();
    }
}
