package com.chess.engine.player.ai;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    public MiniMax(int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        for (Move move: board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance() == Alliance.WHITE ?
                        min(moveTransition.getBoard(), searchDepth - 1) :
                        max(moveTransition.getBoard(), searchDepth - 1);
                if (board.currentPlayer().getAlliance() == Alliance.WHITE &&
                        currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
                else if (board.currentPlayer().getAlliance() == Alliance.BLACK &&
                        currentValue < lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        return bestMove;
    }
    public int min(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (Move move: board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = max(moveTransition.getBoard(), depth - 1);
                if (currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }
    public int max(Board board, int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (Move move: board.currentPlayer().getLegalMoves()) {
            MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                int currentValue = min(moveTransition.getBoard(), depth - 1);
                if (currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;

    }
    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().inCheckmate() || board.currentPlayer().inStalemate();
    }
    public String toString() {
        return "MiniMax";
    }
}
