package Tetris;

import Tetris.Shape.Tetrominoes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {

    private final int BOARD_CELL_WIDTH = 10;
    private final int BOARD_CELL_HEIGHT = 22;
    private Image imageBackground;                                              //Background Image
    private boolean isFalled = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;                                            //Contains the points
    private int currX = 0;
    private int currY = 0;

    Timer mainTimer;
    JLabel statusBar;
    Shape curPiece;
    Tetrominoes[] board;

    public Board(Tetris parent) {
        initBoard();
        setFocusable(true);
        this.curPiece = new Shape();
        this.mainTimer = new Timer(900, this);                                  //Drop speed
        this.mainTimer.start();                                                 //Start dropping
        this.statusBar =  parent.getStatusBar();
        this.board = new Tetrominoes[BOARD_CELL_WIDTH * BOARD_CELL_HEIGHT];     //Board size
        addKeyListener(new Tetris.Board.TAdapter());                                         //Keyboard adapter
        clearBoard();                                                           //Clear all board
    }
    int squareWidth() {
        return 30; }                                                     //Square width and height size
    int squareHeight() {
        return 30; }                                                    //Square width and height size

    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BOARD_CELL_WIDTH) + x]; }

    public void start() {
        if (this.isPaused)
            return;
        this.isStarted = true;
        this.isFalled = false;
        this.numLinesRemoved = 0;
        clearBoard();
        newPiece();
        this.mainTimer.start();
    }                                                   //Set initial values and start main timer
    private void pause() {
        if (!isStarted)
            return;

        this.isPaused = !this.isPaused;
        if (this.isPaused) {
            this.mainTimer.stop();
            this.statusBar.setText("paused");
        } else {
            this.mainTimer.start();
            this.statusBar.setText("Score: "+String.valueOf(numLinesRemoved));
        }
        repaint();
    }                                                  //Pause the game

    private void initBoard() {
        loadImage();
        int width = imageBackground.getWidth(this);
        int height = imageBackground.getHeight(this);
        setPreferredSize(new Dimension(width, height));
    }                                              //Get size for background
    private void loadImage() {
        ImageIcon ii = new ImageIcon("res/background.png");
        imageBackground = ii.getImage();
    }                                              //Load custom background

    public void paintComponent(Graphics g) {

        g.drawImage(imageBackground, 0, 0, null);
    }                                //Override default background
    public void actionPerformed(ActionEvent e) {
        if (isFalled) {
            isFalled = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }                            //Main Action if piece fall down create new piece
    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - this.BOARD_CELL_HEIGHT * squareHeight();


        for (int i = 0; i < this.BOARD_CELL_HEIGHT; ++i) {
            for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j) {
                Tetrominoes shape = shapeAt(j, this.BOARD_CELL_HEIGHT - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = this.currX + this.curPiece.x(i);
                int y = this.currY - this.curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (this.BOARD_CELL_HEIGHT - y - 1) * squareHeight(),
                        this.curPiece.getShape());
            }
        }
    }                                         //Set color to different shapes

    private void dropDown() {
        int newY = this.currY;
        while (newY > 0) {
            if (!tryMove(this.curPiece, this.currX, newY - 1))
                break;
            newY--;
        }
        pieceDropped();
    }                                               //When hit Space drop the shape down as possible
    private void clearBoard() {
        for (int i = 0; i < this.BOARD_CELL_HEIGHT * this.BOARD_CELL_WIDTH; ++i)
            this.board[i] = Tetrominoes.NoShape;
    }                                             //Clear all board
    private void oneLineDown() {
        if (!tryMove(this.curPiece, this.currX, this.currY - 1))
            pieceDropped();
    }                                            //One line down
    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = this.currX + this.curPiece.x(i);
            int y = this.currY - this.curPiece.y(i);
            this.board[(y * this.BOARD_CELL_WIDTH) + x] = this.curPiece.getShape();
        }
        this.numLinesRemoved+=4;
        this.statusBar.setText("Score: "+String.valueOf(numLinesRemoved));
        removeFullLines();

        if (!this.isFalled)
            newPiece();
    }                                           //Piece dropped
    private void newPiece() {
        curPiece.setRandomShape();
        currX = BOARD_CELL_WIDTH / 2 + 1;
        currY = BOARD_CELL_HEIGHT - 1 + curPiece.minY();
        if (!tryMove(this.curPiece, currX, currY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            statusBar.setText("Game Over! Your score is: "+String.valueOf(numLinesRemoved));
            mainTimer.stop();
            isStarted = false;
        }
    }                                               //Generate New Piece
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= this.BOARD_CELL_WIDTH || y < 0 || y >= this.BOARD_CELL_HEIGHT)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        this.curPiece = newPiece;
        this.currX = newX;
        this.currY = newY;
        repaint();
        return true;
    }
    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = this.BOARD_CELL_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < this.BOARD_CELL_HEIGHT - 1; ++k) {
                    for (int j = 0; j < this.BOARD_CELL_WIDTH; ++j)
                        this.board[(k * this.BOARD_CELL_WIDTH) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            this.numLinesRemoved += numFullLines*10;
            this.statusBar.setText("Score: "+String.valueOf(numLinesRemoved));
            this.isFalled = true;
            this.curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {  new Color(33, 65, 198), new Color(215, 15, 55),
                new Color(89, 177, 1), new Color(255, 227, 2),
                new Color(33, 65, 198), new Color(204, 102, 204),
                new Color(227, 91, 2), new Color(255, 15, 139)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }
    class TAdapter extends KeyAdapter
    {
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, currX - 1, currY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, currX + 1, currY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateShape(), currX, currY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_R:
                    start();
                    statusBar.setText("Score: 0");
                    break;
            }

        }
    }
}
