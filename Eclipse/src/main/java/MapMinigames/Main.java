package MapMinigames;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	public static String 	PREFIX = "[MapMinigames]",
							USAGE = PREFIX + " Usage:",
							ONLYPLAYERS = PREFIX + " This command can only be run by players";
	
	public static File configfile;
	
	public static MapMinigames.Snake.Commands snakecommands = new MapMinigames.Snake.Commands();
	public static MapMinigames.Snake.Listeners snakelisteners = new MapMinigames.Snake.Listeners();
	public static MapMinigames.Test.Commands testcommands = new MapMinigames.Test.Commands();
	public static MapMinigames.Tetris.Commands tetriscommands = new MapMinigames.Tetris.Commands();
	public static MapMinigames.Tetris.Listeners tetrislisteners = new MapMinigames.Tetris.Listeners();

	@Override
	public void onEnable() {
		initCommands();
		initListeners();
		initConfig();
		Bukkit.getLogger().log(Level.INFO, PREFIX + " Started");
	}
	
	@Override
	public void onDisable() {
	}
	
	private void initCommands() {
		getCommand("mmsnake").setExecutor(snakecommands);
		getCommand("mmtest").setExecutor(testcommands);
		getCommand("mmtetris").setExecutor(tetriscommands);
	}
	
	private void initListeners() {
		getServer().getPluginManager().registerEvents(snakelisteners, this);
		getServer().getPluginManager().registerEvents(tetrislisteners, this);
	}
	
	private void initConfig(){
		configfile = new File(this.getDataFolder() + File.separator + "config.json");
		String pathconfigfile = configfile.getParent();
		if (!(new File(pathconfigfile).exists())) {
			new File(pathconfigfile).mkdirs();
		}
		try {
			configfile.createNewFile();
		} catch (IOException ioException) {
			getLogger().log(Level.INFO, PREFIX + " " + ioException);
		}
		Config.load(configfile);
		getLogger().log(Level.INFO, PREFIX + "Config loaded");
	}
}
