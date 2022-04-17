package MapMinigames.Snake;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer{
	
	Snake snake;
	
	public Renderer(Player player) {
		snake = new Snake(player);
		snake.startNewGame();
        snake.drawAll();
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		canvas.drawImage(0, 0, snake.getCurrentImage());
	}

}
