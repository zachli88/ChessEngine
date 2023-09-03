package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.List;

public abstract class Piece {
    protected final PieceType pieceType;
    protected final int position;
    private final int cachedHashCode;
    protected final boolean isFirstMove;
    protected final Alliance pieceAlliance;
    public Piece(PieceType pieceType, int position, Alliance pieceAlliance, boolean isFirstMove) {
        this.pieceType = pieceType;
        this.position = position;
        this.pieceAlliance = pieceAlliance;
        this.cachedHashCode = computeHashCode();
        this.isFirstMove = isFirstMove ;
    }
    public abstract Piece movePiece(Move move);
    public PieceType getPieceType() {
        return pieceType;
    }
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        Piece otherPiece = (Piece) other;
        return pieceAlliance == otherPiece.pieceAlliance
                && pieceType == otherPiece.pieceType && position == otherPiece.position;
    }
    private int computeHashCode() {
        int prime = 31;
        int result = pieceType.hashCode();
        result = prime * result + pieceAlliance.hashCode();
        result = prime * result + position;
        return result;
    }
    public String toString() {
        return pieceType.toString();
    }
    public int hashCode() {
        return cachedHashCode;
    }
    public boolean isFirstMove() {
        return isFirstMove;
    }
    public boolean isKing() {
        return pieceType == PieceType.KING;
    }
    public boolean isRook() {
        return pieceType == PieceType.ROOK;
    }
    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }
    public int getPosition() {
        return position;
    }
    public abstract List<Move> getLegalMoves(Board board);

    public int getPieceValue() {
        return pieceType.getPieceValue();
    }

    public enum PieceType {
        PAWN("P", 100),
        KNIGHT("N", 300),
        BISHOP("B", 300),
        ROOK("R", 500),
        QUEEN("Q", 900),
        KING("K", 10000);

        private String pieceName;
        private int pieceValue;
        PieceType(String pieceName, int pieceValue) {
           this.pieceName = pieceName;
           this.pieceValue = pieceValue;
        }
        public String toString() {
            return pieceName;
        }
        public int getPieceValue() {
            return pieceValue;
        }
    }
}
