package com.chess.pgn;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Pawn;

public class FenUtilities {
    private FenUtilities() {
        throw new RuntimeException("not instantiable");
    }
//    public static Board createGameFromFEN(String fenString) {
//
//    }

    public static String createFENFromGame(Board board) {
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculateEnPassantSquare(board) + " 0 1";
    }

    private static String calculateEnPassantSquare(Board board) {
        Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn == null) {
            return "-";
        }
        return Board.getPositionAtCoordinate(enPassantPawn.getPosition() -
                8 * enPassantPawn.getPieceAlliance().getDirection());
    }

    private static String calculateCastleText(Board board) {
        StringBuilder stb = new StringBuilder();
        if (board.whitePlayer().kingSideCastleCapable()) {
            stb.append("K");
        }
        if (board.whitePlayer().queenSideCastleCapable()) {
            stb.append("Q");
        }
        if (board.whitePlayer().kingSideCastleCapable()) {
            stb.append("k");
        }
        if (board.whitePlayer().queenSideCastleCapable()) {
            stb.append("q");
        }
        String result = stb.toString();
        return result.length() == 0 ? "-" : result;
    }

    private static String calculateBoardText(Board board) {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < Board.NUM_SQUARES; i++) {
            stb.append(board.getSquare(i).toString());
        }
        stb.insert(8, "/");
        stb.insert(17, "/");
        stb.insert(26, "/");
        stb.insert(35, "/");
        stb.insert(44, "/");
        stb.insert(53, "/");
        stb.insert(62, "/");
        return stb.toString().replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(Board board) {
        return board.currentPlayer().toString().substring(0, 1).toLowerCase();
    }
}
