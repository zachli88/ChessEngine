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

public class WhitePlayer extends Player {
    public WhitePlayer(Board board, List<Move> whiteLegalMoves, List<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }
    public List<Piece> getActivePieces() {
        return board.getWhitePieces();
    }
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    public String toString() {
        return "White";
    }
    @Override
    public Player getOpponent() {
        return board.blackPlayer();
    }

    @Override
    public List<Move> calculateKingCastle(List<Move> playerLegals, List<Move> opponentLegals) {
        List<Move> kingCastles = new ArrayList<>();
        if (playerKing.isFirstMove() && !this.inCheck()) {
            if (!board.getSquare(61).occupied() && !board.getSquare(62).occupied()) {
                Square rookSquare = board.getSquare(63);
                if (rookSquare.occupied() && rookSquare.getPiece().isRook()
                        && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacks(61, opponentLegals).isEmpty()
                            && Player.calculateAttacks(62, opponentLegals).isEmpty()) {
                        kingCastles.add(new KingSideCastle(board, playerKing, 62,
                                (Rook) rookSquare.getPiece(), rookSquare.getCoordinate(), 61));
                    }
                }
            }
            if (!board.getSquare(59).occupied() && !board.getSquare(58).occupied()
                    && !board.getSquare(57).occupied()) {
                Square rookSquare = board.getSquare(56);
                if (rookSquare.occupied() && rookSquare.getPiece().isRook()
                        && rookSquare.getPiece().isFirstMove()) {
                    if (Player.calculateAttacks(58, opponentLegals).isEmpty()
                            && Player.calculateAttacks(59, opponentLegals).isEmpty()) {
                        kingCastles.add(new QueenSideCastle(board, playerKing, 58,
                                (Rook) rookSquare.getPiece(), rookSquare.getCoordinate(), 59));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}
