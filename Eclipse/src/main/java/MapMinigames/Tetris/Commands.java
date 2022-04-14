package MapMinigames.Tetris;

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
	public static Renderer tetrisrenderer;
	private List<MapRenderer> oldrenderers;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ONLYPLAYERS);
			return true;
		}
		else if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(PREFIX + " -----HELP-----");
			sender.sendMessage(PREFIX + " /mmtetris play <mapid>");
			sender.sendMessage(PREFIX + " /mmtetris stop");
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
			for (String name:Config.getInstance().tetrisscores.keySet()) {
				sender.sendMessage(PREFIX + " " + name + ": " + Config.getInstance().tetrisscores.get(name));
			}
			return true;
		}
		return false;
	}
	
	public boolean play(CommandSender sender, String id) {
		if (active) {
			sender.sendMessage(PREFIX + " Another game of tetris is already running");
			sender.sendMessage(PREFIX + " Please stop map " + mapid + " first");
			return true;
		}
		if (!Config.getInstance().tetrisscores.containsKey(sender.getName())) {
			Config.getInstance().tetrisscores.put(sender.getName(),0);
			Config.getInstance().toFile(configfile);
		}
		player = (Player) sender;
		active = true;
		mapid = Integer.parseInt(id);
		@SuppressWarnings("deprecation")
		MapView tetrismapview = Bukkit.getMap(mapid);
		oldrenderers = tetrismapview.getRenderers();
		for (MapRenderer oldmaprenderer:oldrenderers) {
			tetrismapview.removeRenderer(oldmaprenderer);
		}
		tetrisrenderer = new Renderer(player);
		tetrismapview.addRenderer(tetrisrenderer);
		return true;
	}
	
	public boolean stop(CommandSender sender) {
		if (!active) {
			sender.sendMessage(PREFIX + " Tetris is not running");
			return true;
		}
		active = false;
		@SuppressWarnings("deprecation")
		MapView tetrismapview = Bukkit.getMap(mapid);
		tetrismapview.removeRenderer(tetrisrenderer);
		for (MapRenderer oldmaprenderer:oldrenderers) {
			tetrismapview.addRenderer(oldmaprenderer);
		}
		tetrisrenderer.tetris.pause();
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
