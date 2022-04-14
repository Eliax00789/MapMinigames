package MapMinigames.Test;

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
	
	private Boolean active = false;
	private Integer mapid;
	private Renderer testrenderer;
	private List<MapRenderer> oldrenderers;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ONLYPLAYERS);
			return true;
		}
		else if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(PREFIX + " -----HELP-----");
			sender.sendMessage(PREFIX + " /mmtest play <mapid>");
			sender.sendMessage(PREFIX + " /mmtest stop");
			sender.sendMessage(PREFIX + " --------------");
			return true;
		}
		else if (args[0].equalsIgnoreCase("play")) {
			if (active) {
				sender.sendMessage(PREFIX + " Another instance of test is already running");
				sender.sendMessage(PREFIX + " Please stop " + mapid + " first");
				return true;
			}
			if (!Config.getInstance().testscores.contains(sender.getName())) {
				Config.getInstance().testscores.add(sender.getName());
				Config.getInstance().toFile(configfile);
			}
			active = true;
			mapid = Integer.parseInt(args[1]);
			@SuppressWarnings("deprecation")
			MapView testmapview = Bukkit.getMap(mapid);
			oldrenderers = testmapview.getRenderers();
			for (MapRenderer oldmaprenderer:oldrenderers) {
				testmapview.removeRenderer(oldmaprenderer);
			}
			testrenderer = new Renderer();
			testmapview.addRenderer(testrenderer);
			return true;
		}
		else if (args[0].equalsIgnoreCase("stop")) {
			if (!active) {
				sender.sendMessage(PREFIX + " Test is not running");
				return true;
			}
			active = false;
			@SuppressWarnings("deprecation")
			MapView testmapview = Bukkit.getMap(mapid);
			testmapview.removeRenderer(testrenderer);
			for (MapRenderer oldmaprenderer:oldrenderers) {
				testmapview.addRenderer(oldmaprenderer);
			}
			return true;
		}
		else if (args[0].equalsIgnoreCase("highscores")) {
			for (String name:Config.getInstance().testscores) {
				sender.sendMessage(PREFIX + " " + name);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
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
