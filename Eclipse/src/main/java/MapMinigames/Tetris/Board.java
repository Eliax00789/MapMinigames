package MapMinigames.Tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import org.bukkit.entity.Player;

import MapMinigames.Config;
import MapMinigames.Tetris.Shape.Tetrominoe;

import static MapMinigames.Main.*;

public class Board{
	
	private Player player;

	private final int BOARD_WIDTH = 20;
    private final int BOARD_HEIGHT = 32;
    private final int PERIOD_INTERVAL = 300;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int score = 0;
    private int numLinesRemoved = 0;
    private int numTetrises = 0;
    public int curX = 0;
    public int curY = 0;

    public Shape curPiece;
    private Tetrominoe[] board;
    
    private BufferedImage finalimage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
    private Graphics finalgraphics = finalimage.getGraphics();
    private BufferedImage image = new BufferedImage(80, 128, BufferedImage.TYPE_INT_RGB);
    private Graphics graphics = image.getGraphics();
    private BufferedImage logo = new BufferedImage(48, 20, BufferedImage.TYPE_INT_RGB);
    private Graphics logographics = logo.getGraphics();
    
    public Board(Player play) {
    	player = play;
    	logographics.setColor(Color.RED);
    	logographics.drawString("T", 5, 10);
    	logographics.setColor(Color.ORANGE);
    	logographics.drawString("E", 12, 10);
    	logographics.setColor(Color.YELLOW);
    	logographics.drawString("T", 20, 10);
    	logographics.setColor(Color.GREEN);
    	logographics.drawString("R", 27, 10);
    	logographics.setColor(Color.BLUE);
    	logographics.drawString("I", 35, 10);
    	logographics.setColor(Color.MAGENTA);
    	logographics.drawString("S", 37, 10);
    }

    private Tetrominoe shapeAt(int x, int y) {

        return board[(y * BOARD_WIDTH) + x];
    }

    void start() {

        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();

        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }
    
    private int squareWidth() {

        return 80 / BOARD_WIDTH;
    }

    private int squareHeight() {

        return 128 / BOARD_HEIGHT;
    }

    public void pause() {

        isPaused = !isPaused;

        graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 80, 128);
		graphics.setColor(Color.BLACK);
        for (int i = 0; i < 12; i++) {
            graphics.drawLine(i * 8, 0, i * 8, 128);
        }
        doDrawing(graphics);
    }

    public void dropDown() {

        int newY = curY;

        while (newY > 0) {

            if (!tryMove(curPiece, curX, newY - 1)) {

                break;
            }

            newY--;
        }

        pieceDropped();
    }

    public void oneLineDown() {

        if (!tryMove(curPiece, curX, curY - 1)) {

            pieceDropped();
        }
    }

    private void clearBoard() {

        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {

            board[i] = Tetrominoe.NoShape;
        }
    }

    private void pieceDropped() {

        for (int i = 0; i < 4; i++) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {

            newPiece();
        }
    }

    private void newPiece() {

        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {

            curPiece.setShape(Tetrominoe.NoShape);
            timer.stop();

            player.sendMessage(PREFIX + " Game over. Score: " + score + " Lines: " + numLinesRemoved + " Tetrises: " + numTetrises);
            MapMinigames.Main.tetriscommands.stop(player);
            if (Config.getInstance().tetrisscores.get(player.getName()) < score) {
            		Config.getInstance().tetrisscores.put(player.getName(), score);
            		Config.getInstance().toFile(configfile);
            }
        }
    }

    public boolean tryMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {

                return false;
            }

            if (shapeAt(x, y) != Tetrominoe.NoShape) {

                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 80, 128);
		graphics.setColor(Color.BLACK);
        for (int i = 0; i < 12; i++) {
            graphics.drawLine(i * 8, 0, i * 8, 128);
        }
        doDrawing(graphics);

        return true;
    }

    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {

            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {

                if (shapeAt(j, i) == Tetrominoe.NoShape) {

                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {

                numFullLines++;

                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
        	
            if (numFullLines == 1) {
            	score = score + 40;
            }
            else if (numFullLines == 2) {
            	score = score + 100;
            }
            else if (numFullLines == 3) {
            	score = score + 300;
            }
            else if (numFullLines == 4) {
            	numTetrises++;
            	score = score + 1200;
            }

            numLinesRemoved += numFullLines;
            

            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }

    }

    public void doGameCycle() {
        update();
        graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 80, 128);
		graphics.setColor(Color.BLACK);
        for (int i = 0; i < 12; i++) {
            graphics.drawLine(i * 8, 0, i * 8, 128);
        }
        doDrawing(graphics);
    }

    private void update() {

        if (isPaused) {

            return;
        }

        if (isFallingFinished) {

            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }
    
    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    public BufferedImage getCurrentImage() {
    	finalgraphics.setColor(Color.BLACK);
    	finalgraphics.fillRect(0, 0, 128, 128);
    	finalgraphics.drawImage(image, 48, 0, null);
    	finalgraphics.drawImage(logo, 0, 0, null);
    	finalgraphics.setColor(Color.WHITE);
    	finalgraphics.drawString("S: " + score, 0, 30);
    	finalgraphics.drawString("L: " + numLinesRemoved, 0, 50);
    	finalgraphics.drawString("T: " + numTetrises, 0, 70);
    	return finalimage;
    }
    
    private void doDrawing(Graphics g) {
        int boardTop = 128 - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {

            for (int j = 0; j < BOARD_WIDTH; j++) {

                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Tetrominoe.NoShape) {

                    drawSquare(g, j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);

                drawSquare(g, x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }
    
    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {

        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colors[shape.ordinal()];

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
}