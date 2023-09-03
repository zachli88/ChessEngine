package com.tests.chess.engine.board;

import com.chess.engine.board.Board;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void initialBoard() {

        final Board board = Board.createStandardBoard();
        assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.currentPlayer().inCheck());
        assertFalse(board.currentPlayer().inCheckmate());
        assertFalse(board.currentPlayer().castled());
        assertTrue(board.currentPlayer().kingSideCastleCapable());
        assertTrue(board.currentPlayer().queenSideCastleCapable());
        assertEquals(board.currentPlayer(), board.whitePlayer());
        assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.currentPlayer().getOpponent().inCheck());
        assertFalse(board.currentPlayer().getOpponent().inCheckmate());
        assertFalse(board.currentPlayer().getOpponent().castled());
        assertTrue(board.currentPlayer().getOpponent().kingSideCastleCapable());
        assertTrue(board.currentPlayer().getOpponent().queenSideCastleCapable());
        assertTrue(board.whitePlayer().toString().equals("White"));
        assertTrue(board.blackPlayer().toString().equals("Black"));
    }
}