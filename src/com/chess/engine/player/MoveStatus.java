package com.chess.engine.player;

public enum MoveStatus {
        DONE {
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
            public boolean isDone() {
                return false;
            }
    },
    IN_CHECK {
            public boolean isDone() {
                return false;
            }
    };
    public abstract boolean isDone();
}
