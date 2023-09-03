package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

public interface BoardEvaluator {
    public int evaluate(Board board, int depth);
}
