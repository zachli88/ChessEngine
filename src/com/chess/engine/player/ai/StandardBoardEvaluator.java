package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int CASTLE_BONUS = 60;
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
                scorePlayer(board, board.blackPlayer(), depth);
    }

    private static int scorePlayer(Board board, Player player, int depth) {
        return pieceValue(player) + mobility(player) + check(player) +
                checkMate(player, depth) + castled(player);
    }
    private static int castled(Player player) {
        return player.castled() ? CASTLE_BONUS : 0;
    }

    private static int check(Player player) {
        return player.getOpponent().inCheck() ? CHECK_BONUS : 0;
    }
    private static int checkMate(Player player, int depth) {
        return player.getOpponent().inCheckmate() ? CHECKMATE_BONUS * (depth + 1) : 0;
    }

    private static int mobility(Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(Player player) {
        int pieceValueScore = 0;
         for (Piece piece: player.getActivePieces()) {
             pieceValueScore += piece.getPieceValue();
         }
         return pieceValueScore;
    }
}
