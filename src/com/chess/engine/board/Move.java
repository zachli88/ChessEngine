package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destination;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board, Piece movedPiece, int destination) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destination = destination;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    public Move(Board board, int destination) {
        this.board = board;
        this.destination = destination;
        this.movedPiece = null;
        this.isFirstMove = false;
    }
    public boolean isAttack() {
        return false;
    }
    public boolean isCastleMove() {
        return false;
    }
    public Piece getAttackedPiece() {
        return null;
    }
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + destination;
        result = prime * result + movedPiece.hashCode();
        return result;
    }
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        Move otherMove = (Move) other;
        return destination == otherMove.destination && movedPiece.equals(otherMove.movedPiece);
    }
    public Piece getMovedPiece() {
        return movedPiece;
    }
    public Board execute() {
        Builder builder = new Builder();
        for (Piece currentPiece: board.currentPlayer().getActivePieces()) {
            if (!movedPiece.equals(currentPiece)) {
                builder.setPiece(currentPiece);
            }
        }
        for (Piece currentPiece: board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(currentPiece);
        }
        builder.setPiece(movedPiece.movePiece(this));
        builder.setNextMove(board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static final class MajorMove extends Move {
        public MajorMove(Board board, Piece piece, int destination) {
            super(board, piece, destination);
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof MajorMove && super.equals(other));
        }
        public String toString() {
            return movedPiece.getPieceType().toString() + Board.getPositionAtCoordinate(destination);
        }
    }
    public int getDestination() {
        return destination;
    }
    public static class AttackMove extends Move {
        final Piece attackedPiece;
        public AttackMove(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination);
            this.attackedPiece = attackedPiece;
        }
        public boolean isAttack() {
            return true;
        }
        public Piece getAttackedPiece() {
            return attackedPiece;
        }
        public int hashCode() {
            return attackedPiece.hashCode() + super.hashCode();
        }
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(other) && attackedPiece.equals(otherAttackMove.attackedPiece);
        }
    }
    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(Board board, Piece pieceMoved, int destination, Piece pieceAttacked) {
            super(board, pieceMoved, destination, pieceAttacked);
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof MajorAttackMove && super.equals(other));
        }
        public String toString() {
            return movedPiece.getPieceType().toString() + getPositionAtCoordinate(destination);
        }
    }
    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece piece, int destination) {
            super(board, piece, destination);
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof PawnMove && super.equals(other));
        }
        public String toString() {
            return getPositionAtCoordinate(destination);
        }
    }
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination, attackedPiece);
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof PawnAttackMove && super.equals(other));
        }
        public String toString() {
            return getPositionAtCoordinate(movedPiece.getPosition()).substring(0, 1) + "x" +
                    getPositionAtCoordinate(destination);
        }
    }
    public static class PawnPromotion extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;
        public PawnPromotion(Move decoratedMove) {
            super(decoratedMove.board, decoratedMove.movedPiece, decoratedMove.destination);
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.movedPiece;
        }
        public boolean isAttack() {
            return decoratedMove.isAttack();
        }
        public Piece getAttackedPiece() {
            return decoratedMove.getAttackedPiece();
        }
        public String toString() {
            return "";
        }
        public int hashCode() {
            return decoratedMove.hashCode() + 31 * promotedPawn.hashCode();
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof PawnPromotion && super.equals(other));
        }
        public Board execute() {
            Board pawnMovedBoard = decoratedMove.execute();
            Builder builder = new Builder();
            for (Piece piece: pawnMovedBoard.currentPlayer().getActivePieces()) {
                if (!promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece: pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(promotedPawn.getPromotionPiece().movePiece(this));
            builder.setNextMove(pawnMovedBoard.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    public static final class PawnEnPassant extends PawnAttackMove {
        public PawnEnPassant(Board board, Piece piece, int destination, Piece attackedPiece) {
            super(board, piece, destination, attackedPiece);
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof PawnEnPassant && super.equals(other));
        }
        public Board execute() {
            Builder builder = new Builder();
            for (Piece piece: board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece: board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(attackedPiece)) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setNextMove(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }
    public static final class PawnJump extends Move {
        public PawnJump(Board board, Piece piece, int destination) {
            super(board, piece, destination);
        }
        public Board execute() {
            Builder builder = new Builder();
            for (Piece piece: board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(Piece piece: board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            Pawn movedPawn = (Pawn) movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setNextMove(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        public String toString() {
            return ALGEBRAIC_NOTATION.get(destination);
        }
    }
    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(Board board, Piece piece, int destination,
                          Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, piece, destination);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }
        public Rook getCastleRook() {
            return castleRook;
        }
        public boolean isCastleMove() {
            return true;
        }
        public Board execute() {
            Builder builder = new Builder();
            for (Piece currentPiece: board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(currentPiece) && !castleRook.equals(currentPiece)) {
                    builder.setPiece(currentPiece);
                }
            }
            for (Piece currentPiece: board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(currentPiece);
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setPiece(new Rook(castleRook.getPieceAlliance(), castleRookDestination));
            builder.setNextMove(board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + castleRook.hashCode();
            result = prime * result + castleRookDestination;
            return result;
        }
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && castleRook.equals(otherCastleMove.castleRook);
        }
    }
    public static final class KingSideCastle extends CastleMove {
        public KingSideCastle(Board board, Piece piece, int destination,
                              Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, piece, destination, castleRook, castleRookStart, castleRookDestination);
        }
        public String toString() {
            return "O-O";
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof KingSideCastle && super.equals(other));
        }
    }
    public static final class QueenSideCastle extends CastleMove {
        public QueenSideCastle(Board board, Piece piece, int destination,
                               Rook castleRook, int castleRookStart, int castleRookDestination) {
            super(board, piece, destination, castleRook, castleRookStart, castleRookDestination);
        }
        public String toString() {
            return "O-O-O";
        }
        public boolean equals(Object other) {
            return this == other || (other instanceof QueenSideCastle && super.equals(other));
        }
    }
    public static final class NullMove extends Move {
        public NullMove() {
            super(null, -1);
        }
        public Board execute() {
            throw new RuntimeException("cannot execute null move");
        }
    }
    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("not instantiable");
        }
        public static Move createMove(Board board, int currentCoordinate, int destination) {
            for (Move move: board.getLegalMoves()) {
                if (move.movedPiece.getPosition() == currentCoordinate
                && move.destination == destination) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
