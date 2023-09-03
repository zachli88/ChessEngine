package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

import java.util.*;

public class Board {
    public static final int NUM_SQUARES = 64;
    public static final int NUM_SQUARES_PER_ROW = 8;
    private final List<Square> gameBoard;
    private final List<Piece> blackPieces;
    private final List<Piece> whitePieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
    public static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();

    public static final Map<String, Integer> POSITION_TO_COORDINATE = initalizePositionToCoordinate();


    private Board(Builder builder) {
        gameBoard = createBoard(builder);
        whitePieces = calculateActivePieces(gameBoard, Alliance.WHITE);
        blackPieces = calculateActivePieces(gameBoard, Alliance.BLACK);
        enPassantPawn = builder.enPassantPawn;
        final List<Move> whiteLegalMoves = calculateLegalMoves(whitePieces);
        final List<Move> blackLegalMoves = calculateLegalMoves(blackPieces);
        whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
        currentPlayer = builder.nextMove.choosePlayer(whitePlayer, blackPlayer);
    }
    private List<Move> calculateLegalMoves(List<Piece> pieces) {
        List<Move> legalMoves = new ArrayList<>();
        for (Piece piece: pieces) {
            legalMoves.addAll(piece.getLegalMoves(this));
        }
        return Collections.unmodifiableList(legalMoves);
    }
    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }
    private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }
    private static Map<String, Integer> initalizePositionToCoordinate() {
        Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }
    public Player whitePlayer() {
        return whitePlayer;
    }
    public Player blackPlayer() {
        return blackPlayer;
    }
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < NUM_SQUARES; i++) {
            String squareText = gameBoard.get(i).toString();
            stb.append(String.format("%3s", squareText));
            if((i + 1) % NUM_SQUARES_PER_ROW == 0) {
                stb.append("\n");
            }
        }
        return stb.toString();
    }
    public static int getCoordinateAtPosition(String position) {
        return POSITION_TO_COORDINATE.get(position);
    }
    public static String getPositionAtCoordinate(int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }
    public List<Piece> getBlackPieces() {
        return blackPieces;
    }
    public List<Piece> getWhitePieces() {
        return whitePieces;
    }
    private static List<Piece> calculateActivePieces(List<Square> gameBoard, Alliance alliance) {
        List<Piece> activePieces = new ArrayList<>();
        for (Square sq: gameBoard) {
            Piece piece = sq.getPiece();
            if (piece != null && piece.getPieceAlliance() == alliance) {
                activePieces.add(piece);
            }
        }
        return Collections.unmodifiableList(activePieces);
    }
    private static List<Square> createBoard(Builder builder) {
        final ArrayList<Square> squares = new ArrayList<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            squares.add(Square.createSquare(i, builder.boardConfig.get(i)));
        }
        return Collections.unmodifiableList(squares);
    }
    public static Board createStandardBoard() {
        Builder builder = new Builder();
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setNextMove(Alliance.WHITE);
        return builder.build();
    }
    public Square getSquare(int coordinate) {
        return gameBoard.get(coordinate);
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public List<Move> getLegalMoves() {
        List<Move> result = new ArrayList<>();
        result.addAll(whitePlayer.getLegalMoves());
        result.addAll(blackPlayer.getLegalMoves());
        return result;
    }

    public static class Builder {
        Map<Integer, Piece> boardConfig;
        Alliance nextMove;
        Pawn enPassantPawn;
        public Builder() {
            boardConfig = new HashMap<>();
        }
        public Builder setPiece(Piece piece) {
            boardConfig.put(piece.getPosition(), piece);
            return this;
        }
        public Builder setNextMove(Alliance nextMove) {
            this.nextMove = nextMove;
            return this;
        }
        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn movedPawn) {
            enPassantPawn = movedPawn;
        }
    }
}
