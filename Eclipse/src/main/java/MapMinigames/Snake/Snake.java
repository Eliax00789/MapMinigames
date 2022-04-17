package MapMinigames.Snake;

import static MapMinigames.Main.configfile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import MapMinigames.Config;

public class Snake implements Runnable{
   enum Dir {
      up(0, -1), right(1, 0), down(0, 1), left(-1, 0);
 
      Dir(int x, int y) {
         this.x = x; this.y = y;
      }
 
      final int x, y;
   }
 
   static final Random rand = new Random();
   static final int WALL = -1;
   static final int MAX_ENERGY = 1500;
 
   volatile boolean gameOver = true;
 
   Thread gameThread;
   int score;
   int nRows = 32;
   int nCols = 32;
   Dir dir;
   int energy;
 
   int[][] grid;
   List<Point> snake, treats;
   Font smallFont;
   
   BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
   Graphics graphics = image.getGraphics();
   
   Player player;
 
   public Snake(Player play) {
	  player = play;
	  graphics.setColor(Color.WHITE);
	  graphics.fillRect(0, 0, 128, 128);
      initGrid();
   }
 
   void startNewGame() {
      gameOver = false;
 
      stop();
      initGrid();
      treats = new LinkedList<>();
 
      dir = Dir.left;
      energy = MAX_ENERGY;
 
      score = 0;
 
      snake = new ArrayList<>();
      for (int x = 0; x < 7; x++)
         snake.add(new Point(nCols / 2 + x, nRows / 2));
 
      do
         addTreat();
      while(treats.isEmpty());
 
      (gameThread = new Thread(this)).start();
   }
 
   void stop() {
      if (gameThread != null) {
         Thread tmp = gameThread;
         gameThread = null;
         tmp.interrupt();
      }
   }
 
   void initGrid() {
      grid = new int[nRows][nCols];
      for (int r = 0; r < nRows; r++) {
         for (int c = 0; c < nCols; c++) {
            if (c == 0 || c == nCols - 1 || r == 0 || r == nRows - 1)
               grid[r][c] = WALL;
         }
      }
   }
 
   @Override
   public void run() {
 
      while (Thread.currentThread() == gameThread) {
 
         try {
            Thread.sleep(Math.max(200 - score, 50));
         } catch (InterruptedException e) {
            return;
         }
 
         if (energyUsed() || hitsWall() || hitsSnake()) {
            gameOver();
            if (Config.getInstance().snakescores.get(player.getName()) < score) {
        		Config.getInstance().snakescores.put(player.getName(), score);
        		Config.getInstance().toFile(configfile);
        }
         } else {
            if (eatsTreat()) {
               score++;
               energy = MAX_ENERGY;
               growSnake();
               addTreat();
            }
            moveSnake();
         }
         drawAll();
      }
   }
 
   boolean energyUsed() {
      energy -= 10;
      return energy <= 0;
   }
 
   boolean hitsWall() {
      Point head = snake.get(0);
      int nextCol = head.x + dir.x;
      int nextRow = head.y + dir.y;
      return grid[nextRow][nextCol] == WALL;
   }
 
   boolean hitsSnake() {
      Point head = snake.get(0);
      int nextCol = head.x + dir.x;
      int nextRow = head.y + dir.y;
      for (Point p : snake)
         if (p.x == nextCol && p.y == nextRow)
            return true;
      return false;
   }
 
   boolean eatsTreat() {
      Point head = snake.get(0);
      int nextCol = head.x + dir.x;
      int nextRow = head.y + dir.y;
      for (Point p : treats)
         if (p.x == nextCol && p.y == nextRow) {
            return treats.remove(p);
         }
      return false;
   }
 
   void gameOver() {
      gameOver = true;
      stop();
   }
 
   void moveSnake() {
      for (int i = snake.size() - 1; i > 0; i--) {
         Point p1 = snake.get(i - 1);
         Point p2 = snake.get(i);
         p2.x = p1.x;
         p2.y = p1.y;
      }
      Point head = snake.get(0);
      head.x += dir.x;
      head.y += dir.y;
   }
 
   void growSnake() {
      Point tail = snake.get(snake.size() - 1);
      int x = tail.x + dir.x;
      int y = tail.y + dir.y;
      snake.add(new Point(x, y));
   }
 
   void addTreat() {
	  int x, y;
      while (true) {
      x = rand.nextInt(nCols);
      y = rand.nextInt(nRows);
      if (grid[y][x] != 0)
          continue;
      Point p = new Point(x, y);
      if (snake.contains(p) || treats.contains(p))
          continue;
      treats.add(p);
      break;
      }
   }
   
   public BufferedImage getCurrentImage() {
	  return image;
   }
 
   void drawGrid(Graphics2D g) {
      g.setColor(Color.WHITE);
      for (int r = 0; r < nRows; r++) {
         for (int c = 0; c < nCols; c++) {
            if (grid[r][c] == WALL)
               g.fillRect(c * 4, r * 4, 4, 4);
         }
      }
   }
 
   void drawSnake(Graphics2D g) {
      g.setColor(Color.RED);
      for (Point p : snake)
         g.fillRect(p.x * 4, p.y * 4, 4, 4);
 
      g.setColor(energy < 500 ? Color.ORANGE : Color.BLUE);
      Point head = snake.get(0);
      g.fillRect(head.x * 4, head.y * 4, 4, 4);
   }
 
   void drawTreats(Graphics2D g) {
      g.setColor(Color.green);
      for (Point p : treats)
         g.fillRect(p.x * 4, p.y * 4, 4, 4);
   }

   void drawStartScreen(Graphics2D g) {
      g.setColor(Color.WHITE);
      g.drawString("SNAKE", 10, 19);
      g.setColor(Color.YELLOW);
      g.drawString("(Click To START)", 10, 29);
   }

   void drawScore(Graphics2D g) {
      g.setColor(Color.WHITE);
      g.drawString("Score: " + score, 10, 19);
      g.drawString("Energy: " + energy, 10, 29);
   }

   public void drawAll() {
	  Graphics2D g = (Graphics2D) graphics;
	  g.setColor(Color.BLACK);
	  g.fillRect(0, 0, 128, 128);
      drawGrid(g);
      if (gameOver) {
         drawStartScreen(g);
      } else {
         drawScore(g);
         drawTreats(g);
         drawSnake(g);
      }
      graphics = (Graphics) g;
   }
}