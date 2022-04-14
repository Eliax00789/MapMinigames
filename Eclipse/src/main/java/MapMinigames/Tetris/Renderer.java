package MapMinigames.Tetris;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer {
	
	public Board tetris;
	
	public Renderer(Player player){
		tetris = new Board(player);
		tetris.start();
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		canvas.drawImage(0, 0, tetris.getCurrentImage());
	}
}