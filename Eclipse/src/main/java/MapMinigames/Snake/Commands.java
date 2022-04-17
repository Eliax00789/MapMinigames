package MapMinigames.Snake;

import static MapMinigames.Main.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import MapMinigames.Config;

public class Commands implements TabExecutor{
	
	public static Boolean active = false;
	public static Player player;
	private Integer mapid;
	public static Renderer snakerenderer;
	private List<MapRenderer> oldrenderers;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ONLYPLAYERS);
			return true;
		}
		else if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(PREFIX + " -----HELP-----");
			sender.sendMessage(PREFIX + " /mmsnake play <mapid>");
			sender.sendMessage(PREFIX + " /mmsnake stop");
			sender.sendMessage(PREFIX + " --------------");
			return true;
		}
		else if (args[0].equalsIgnoreCase("play")) {
			play(sender, args[1]);
			return true;
		}
		else if (args[0].equalsIgnoreCase("stop")) {
			stop(sender);
			return true;
		}
		else if (args[0].equalsIgnoreCase("highscores")) {
			for (String name:Config.getInstance().snakescores.keySet()) {
				sender.sendMessage(PREFIX + " " + name + ": " + Config.getInstance().snakescores.get(name));
			}
			return true;
		}
		return false;
	}
	
	public boolean play(CommandSender sender, String id) {
		if (active) {
			sender.sendMessage(PREFIX + " Another game of snake is already running");
			sender.sendMessage(PREFIX + " Please stop map " + mapid + " first");
			return true;
		}
		if (!Config.getInstance().snakescores.containsKey(sender.getName())) {
			Config.getInstance().snakescores.put(sender.getName(),0);
			Config.getInstance().toFile(configfile);
		}
		player = (Player) sender;
		active = true;
		mapid = Integer.parseInt(id);
		@SuppressWarnings("deprecation")
		MapView snakemapview = Bukkit.getMap(mapid);
		oldrenderers = snakemapview.getRenderers();
		for (MapRenderer oldmaprenderer:oldrenderers) {
			snakemapview.removeRenderer(oldmaprenderer);
		}
		snakerenderer = new Renderer(player);
		snakemapview.addRenderer(snakerenderer);
		return true;
	}
	
	public boolean stop(CommandSender sender) {
		if (!active) {
			sender.sendMessage(PREFIX + " Snake is not running");
			return true;
		}
		active = false;
		@SuppressWarnings("deprecation")
		MapView snakemapview = Bukkit.getMap(mapid);
		snakemapview.removeRenderer(snakerenderer);
		for (MapRenderer oldmaprenderer:oldrenderers) {
			snakemapview.addRenderer(oldmaprenderer);
		}
		// tetrisrenderer.tetris.pause();
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		List<String> toCompleteTo = new ArrayList<String>();
		List<String> commands = new ArrayList<String>();
		commands.add("help");
		commands.add("play");
		commands.add("stop");
		commands.add("highscores");
		if (args.length == 1) {
			for (String command:commands) {
				if (command.contains(args[0].toLowerCase())) {
					toCompleteTo.add(command);
				}
			}
		}
		Collections.sort(toCompleteTo);
		return toCompleteTo;
	}

}
