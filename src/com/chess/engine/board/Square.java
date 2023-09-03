package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Piece;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class Square {
    protected final int coordinate;
    private static final Map<Integer, EmptySquare> EMPTY_SQUARE_MAP = createEmptySquares();
    private static Map<Integer, EmptySquare> createEmptySquares() {
        final Map<Integer, EmptySquare> emptySquareMap = new HashMap<>();
        for(int i = 0; i < Board.NUM_SQUARES; i++) {
            emptySquareMap.put(i, new EmptySquare(i));
        }
        return Collections.unmodifiableMap(emptySquareMap);
    }
    public int getCoordinate() {
        return coordinate;
    }
    private Square(int coordinate) {
        this.coordinate = coordinate;
    }
    public static Square createSquare(int coordinate, Piece piece) {
        return piece == null ? EMPTY_SQUARE_MAP.get(coordinate) : new OccupiedSquare(coordinate, piece);
    }
    public abstract boolean  occupied();
    public abstract Piece getPiece();

    public static class EmptySquare extends Square {
        private EmptySquare(int coordinate) {
            super(coordinate);
        }
        public boolean occupied() {
            return false;
        }
        public Piece getPiece() {
            return null;
        }
        public String toString() {
            return "-";
        }
    }
    public static class OccupiedSquare extends Square {
        private final Piece piece;
        private OccupiedSquare(int coordinate, Piece piece) {
            super(coordinate);
            this.piece = piece;
        }
        public boolean occupied() {
            return true;
        }
        public Piece getPiece() {
            return piece;
        }
        public String toString() {
            return piece.getPieceAlliance() == Alliance.BLACK ?
                    piece.toString().toLowerCase() : piece.toString();
        }
    }
}
