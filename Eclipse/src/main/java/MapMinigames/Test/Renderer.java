package MapMinigames.Test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer {
	
	long currentFrame = 1;
	long lastFrame = System.currentTimeMillis();
	
	int side = 2;
	
	Random rand = new Random(new Random().nextInt());
	int x = rand.nextInt(128 - side * 2) + side;
	int y = rand.nextInt(128 - side * 2) + side;
	int vy = 50;
	int vx = 50;

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		currentFrame = System.currentTimeMillis();
		long deltaTime = currentFrame - lastFrame;
		float speedModifier = (float) deltaTime / 1000;
		lastFrame = currentFrame;
		
		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		x += vx * speedModifier;
		y += vy * speedModifier;

		if (x - side < 0) {
		    x = side;
		    vx *= -1;
		}
		if (x + side > 128) {
		    x = 128 - side;
		    vx *= -1;
		}

		if (y - side < 0) {
		    y = side;
		    vy *= -1;
		}
		if (y + side > 128) {
		    y = 128 - side;
		    vy *= -1;
		}

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 128, 128);

		g.setColor(Color.WHITE);
		g.fillRect(x - side, y - side, 2 * side, 2 * side);
		
		canvas.drawImage(0, 0, image);
	}

}