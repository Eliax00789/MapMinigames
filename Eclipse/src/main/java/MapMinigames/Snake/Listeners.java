package MapMinigames.Snake;

import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import MapMinigames.Snake.Snake.Dir;

public class Listeners implements Listener{
	
	private Boolean dropDown = true;
	
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (Commands.active && event.getPlayer().equals(Commands.player) && event.getStatistic() == Statistic.JUMP) {
        	Location loc = event.getPlayer().getLocation();
        	if (Commands.snakerenderer.snake.dir != Dir.down)
                Commands.snakerenderer.snake.dir = Dir.up;
            event.getPlayer().teleport(loc);
        	event.setCancelled(true);
        }
    }
	
	@EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		if (Commands.active && event.getPlayer().equals(Commands.player) && event.getPlayer().isSneaking()) {
			if (dropDown) {
				if (Commands.snakerenderer.snake.dir != Dir.up)
	                Commands.snakerenderer.snake.dir = Dir.down;
				dropDown = !dropDown;
				event.setCancelled(true);
			}
			else {
				dropDown = !dropDown;
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent event) {
		if (Commands.active && event.getPlayer().equals(Commands.player)) {
			if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if (Commands.snakerenderer.snake.gameOver) {
	                  Commands.snakerenderer.snake.startNewGame();
	                  Commands.snakerenderer.snake.drawAll();
	               }
				if (Commands.snakerenderer.snake.dir != Dir.right)
	                Commands.snakerenderer.snake.dir = Dir.left;
				event.setCancelled(true);
			}
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (Commands.snakerenderer.snake.dir != Dir.left)
	                Commands.snakerenderer.snake.dir = Dir.right;
				event.setCancelled(true);
			}
		}
	}
}
