package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final List<Move> legalMoves;
    private final boolean inCheck;
    Player(Board board, List<Move> legalMoves, List<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = new ArrayList<>();
        for (Move move: legalMoves) {
            this.legalMoves.add(move);
        }
        for (Move move: calculateKingCastle(legalMoves, opponentMoves)) {
            this.legalMoves.add(move);
        }
        inCheck = !Player.calculateAttacks(playerKing.getPosition(), opponentMoves).isEmpty();
    }

    protected static List<Move> calculateAttacks(int position, List<Move> opponentMoves) {
        List<Move> attackMoves = new ArrayList<>();
        for (Move move: opponentMoves) {
            if (position == move.getDestination()) {
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private King establishKing() {
        for (Piece piece: getActivePieces()) {
            if (piece.isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board");
    }
    public boolean isMoveLegal(Move move) {
        return legalMoves.contains(move);
    }
    public boolean inCheck() {
        return inCheck;
    }
    public boolean inCheckmate() {
        return inCheck && !hasEscapeMoves();
    }
    public List<Move> getLegalMoves() {
        return legalMoves;
    }
    private boolean hasEscapeMoves() {
        for (Move move: legalMoves) {
            MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean inStalemate() {
        return !inCheck && !hasEscapeMoves();
    }
    public boolean castled() {
        return false;
    }
    public boolean kingSideCastleCapable() {
        return playerKing.kingSideCastleCapable();
    }
    public boolean queenSideCastleCapable() {
        return playerKing.queenSideCastleCapable();
    }
    public MoveTransition makeMove(Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(board, move, MoveStatus.ILLEGAL_MOVE);
        }
        Board transitionBoard = move.execute();
        final List<Move> kingAttacks = Player.calculateAttacks(transitionBoard.currentPlayer()
                .getOpponent().playerKing.getPosition(), transitionBoard.currentPlayer().legalMoves);
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(board, move, MoveStatus.IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }
    public abstract List<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract List<Move> calculateKingCastle(List<Move> playerLegals, List<Move> opponentLegals);
}
