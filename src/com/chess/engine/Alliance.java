package com.chess.engine;

import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

public enum Alliance {
    WHITE {

        public int getDirection() {
            return -1;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return position >= 0 && position <= 7;
        }
    },
    BLACK {
        public int getDirection() {
            return 1;
        }
        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return position >= 56 && position <= 63;
        }
    };
    public abstract int getDirection();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
    public abstract boolean isPawnPromotionSquare(int position);
}
