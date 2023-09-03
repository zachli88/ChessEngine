package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Square;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private Square sourceSquare;
    private Square destinationSquare;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;
    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension SQUARE_PANEL_DIMENSION = new Dimension(10, 10);
    private static String defaultPieceImagePath = "art/holywarriors/";
    private final Color lightSquareColor = Color.decode("#FFFACD");
    private final Color darkSquareColor = Color.decode("#593E1A");
    private static final Table INSTANCE = new Table();

    private Table() {
        this.gameFrame = new JFrame( "Jchess");
        this.gameFrame.setLayout(new BorderLayout());
        JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        this.gameFrame.add(takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }
    public void show() {
        moveLog.clear();
        gameHistoryPanel.redo(chessBoard, moveLog);
        takenPiecesPanel.redo(moveLog);
        boardPanel.drawBoard(chessBoard);
    }
    public static Table get() {
        return INSTANCE;
    }
    private void setupUpdate(GameSetup gameSetup) {
        setChanged();
        notifyObservers();
    }
    private static class TableGameAIWatcher implements Observer {
        public void update(Observable o, Object arg) {
            if (Table.get().gameSetup.isAIPlayer(Table.get().chessBoard.currentPlayer()) &&
            !Table.get().chessBoard.currentPlayer().inCheckmate() &&
            !Table.get().chessBoard.currentPlayer().inStalemate()) {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            if (Table.get().chessBoard.currentPlayer().inCheckmate()) {
                System.out.println("Game Over, " + Table.get().chessBoard.currentPlayer() + " checkmated");
            }
            else if (Table.get().chessBoard.currentPlayer().inStalemate()) {
                System.out.println("Game Over, " + Table.get().chessBoard.currentPlayer() + " stalemated");
            }
        }
    }
    public void updateGameBoard(Board board) {
        chessBoard = board;
    }
    public void updateComputerMove(Move move) {
        computerMove = move;
    }
    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }
    private static class AIThinkTank extends SwingWorker<Move, String> {
        private AIThinkTank() {

        }

        @Override
        protected Move doInBackground() throws Exception {
            MoveStrategy miniMax = new MiniMax(4);
            return miniMax.execute(Table.get().chessBoard);
        }
        @Override
        public void done() {
            try {
                Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().chessBoard.currentPlayer().makeMove(bestMove).getBoard());
                Table.get().moveLog.addMove(bestMove);
                Table.get().gameHistoryPanel.redo(Table.get().chessBoard, Table.get().moveLog);
                Table.get().takenPiecesPanel.redo(Table.get().moveLog);
                Table.get().boardPanel.drawBoard(Table.get().chessBoard);
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }
    public enum PlayerType {
        HUMAN,
        COMPUTER;
    }
    private JMenu createOptionsMenu() {
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().gameSetup.promptUser();
                Table.get().setupUpdate(Table.get().gameSetup);
            }
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up PGN file");
            }
        });
        fileMenu.add(openPGN);
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMovesHighlightCheckbox =
                new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMovesHighlightCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMovesHighlightCheckbox.isSelected();
            }
        });
        preferencesMenu.add(legalMovesHighlightCheckbox);
        return preferencesMenu;
    }
    public enum BoardDirection {
        NORMAL {
            List<SquarePanel> traverse(List<SquarePanel> boardSquares) {
                return boardSquares;
            }
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            List<SquarePanel> traverse(List<SquarePanel> boardSquares) {
                List<SquarePanel> result = new ArrayList<>();
                for (int i = boardSquares.size() - 1; i >= 0; i--) {
                    result.add(boardSquares.get(i));
                }
                return result;
            }
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<SquarePanel> traverse(List<SquarePanel> boardSquares);
        abstract BoardDirection opposite();
    }
    private class BoardPanel extends JPanel {
        private final List<SquarePanel> boardSquares;
        public BoardPanel() {
            super(new GridLayout(8, 8));
            boardSquares = new ArrayList<>();
            for (int i = 0; i < Board.NUM_SQUARES; i++) {
                SquarePanel squarePanel = new SquarePanel(this, i);
                boardSquares.add(squarePanel);
                add(squarePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(Board board) {
            removeAll();
            for (SquarePanel squarePanel: boardDirection.traverse(boardSquares)) {
                squarePanel.drawSquare(board);
                add(squarePanel);
            }
            validate();
            repaint();
        }
    }
    public static class MoveLog {
        private final List<Move> moves;
        public MoveLog() {
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves() {
            return moves;
        }
        public void addMove(Move move) {
            moves.add(move);
        }
        public int size() {
            return moves.size();
        }
        public void clear() {
            moves.clear();
        }
        public boolean removeMove(Move move) {
            return moves.remove(move);
        }
        public Move removeMove(int index) {
            return moves.remove(index);
        }
    }
    private class SquarePanel extends JPanel {
        private final int squareID;
        public SquarePanel(BoardPanel boardPanel, int squareID) {
            super(new GridBagLayout());
            this.squareID = squareID;
            setPreferredSize(SQUARE_PANEL_DIMENSION);
            assignSquareColor();
            assignSquarePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                        if (sourceSquare == null) {
                            sourceSquare = chessBoard.getSquare(squareID);
                            humanMovedPiece = sourceSquare.getPiece();
                            if (humanMovedPiece == null) {
                                sourceSquare = null;
                            }
                        }
                        else {
                            destinationSquare = chessBoard.getSquare(squareID);
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                    sourceSquare.getCoordinate(), destinationSquare.getCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getBoard();
                                moveLog.addMove(move);
                            }
                            sourceSquare = null;
                            destinationSquare = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                    moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                    else if (isRightMouseButton(e)) {
                        sourceSquare = null;
                        destinationSquare = null;
                        humanMovedPiece = null;
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            validate();
        }
        public void drawSquare(Board board) {
            assignSquareColor();
            assignSquarePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }
        private void assignSquarePieceIcon(Board board) {
            removeAll();
            if (board.getSquare(squareID).occupied()) {
                try {
                    BufferedImage image = ImageIO.read(new File(defaultPieceImagePath +
                            board.getSquare(squareID).getPiece().getPieceAlliance().toString().substring(0, 1) +
                            board.getSquare(squareID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        private void highlightLegalMoves(Board board) {
            if (highlightLegalMoves) {
                for (Move move: pieceLegalMoves(board)) {
                    if (move.getDestination() == squareID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private List<Move> pieceLegalMoves(Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                List<Move> result = new ArrayList<>();
                List<Move> legals = humanMovedPiece.getLegalMoves(board);
                result.addAll(legals);
                if (sourceSquare.getPiece().isKing()) {
                    result.addAll(chessBoard.currentPlayer().calculateKingCastle(legals,
                            chessBoard.currentPlayer().getOpponent().getLegalMoves()));
                }
                return result;
            }
            return Collections.emptyList();
        }
        private void assignSquareColor() {
            int row = squareID / 8 + 1;
            if (row % 2 == 1) {
                setBackground(squareID % 2 == 0 ? lightSquareColor : darkSquareColor);
            }
            else if (row % 2 == 0) {
                setBackground(squareID % 2 == 1 ? lightSquareColor : darkSquareColor);
            }
        }
    }

}
