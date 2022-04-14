package MapMinigames.Tetris;

import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Listeners implements Listener{
	
	private boolean dropDown = false;
	
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (Commands.active && event.getPlayer().equals(Commands.player) && event.getStatistic() == Statistic.JUMP) {
        	Location loc = event.getPlayer().getLocation();
        	Commands.tetrisrenderer.tetris.tryMove(Commands.tetrisrenderer.tetris.curPiece.rotateLeft(), Commands.tetrisrenderer.tetris.curX, Commands.tetrisrenderer.tetris.curY);
            event.getPlayer().teleport(loc);
        	event.setCancelled(true);
        }
    }
	
	@EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		if (Commands.active && event.getPlayer().equals(Commands.player) && event.getPlayer().isSneaking()) {
			if (dropDown) {
				Commands.tetrisrenderer.tetris.dropDown();
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
				Commands.tetrisrenderer.tetris.tryMove(Commands.tetrisrenderer.tetris.curPiece, Commands.tetrisrenderer.tetris.curX - 1, Commands.tetrisrenderer.tetris.curY);
				event.setCancelled(true);
			}
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				Commands.tetrisrenderer.tetris.tryMove(Commands.tetrisrenderer.tetris.curPiece, Commands.tetrisrenderer.tetris.curX + 1, Commands.tetrisrenderer.tetris.curY);
				event.setCancelled(true);
			}
		}
	}
}
