package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board, List<Move> whiteLegalMoves, List<Move> blackLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }
    public List<Piece> getActivePieces() {
        return board.getBlackPieces();
    }
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }
    public String toString() {
        return "Black";
    }

    @Override
    public Player getOpponent() {
        return board.whitePlayer();
    }

    @Override
    public List<Move> calculateKingCastle(List<Move> playerLegals, List<Move> opponentLegals) {
        List<Move> kingCastles = new ArrayList<>();
        if (playerKing.isFirstMove() && !this.inCheck()) {
            if (!board.getSquare(5).occupied() && !board.getSquare(6).occupied()) {
                Square rookSquare = board.getSquare(7);
                if (rookSquare.occupied() && rookSquare.getPiece().isRook()
                        && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacks(5, opponentLegals).isEmpty()
                            && Player.calculateAttacks(6, opponentLegals).isEmpty()) {
                        kingCastles.add(new KingSideCastle(board, playerKing, 6,
                                (Rook) rookSquare.getPiece(), rookSquare.getCoordinate(), 5));
                    }
                }
            }
            if (!board.getSquare(1).occupied() && !board.getSquare(2).occupied()
                    && !board.getSquare(3).occupied()) {
                Square rookSquare = board.getSquare(0);
                if (rookSquare.occupied() && rookSquare.getPiece().isRook()
                        && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacks(3, opponentLegals).isEmpty()
                            && Player.calculateAttacks(2, opponentLegals).isEmpty()) {
                        kingCastles.add(new QueenSideCastle(board, playerKing, 2,
                                (Rook) rookSquare.getPiece(), rookSquare.getCoordinate(), 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
