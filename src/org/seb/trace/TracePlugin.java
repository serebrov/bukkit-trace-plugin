package org.seb.trace;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.seb.trace.TracePluginPlayerListener;


public class TracePlugin extends JavaPlugin {

	Logger log = Logger.getLogger("Minecraft");
	
	private final TracePluginPlayerListener playerListener = new TracePluginPlayerListener(this);
	
	private Map<Player, Material> traceMaterial;
	private Map<Player, Map<String, Location>> tracePoints;
	
	private boolean isDebug = true;
	
	public void onEnable(){ 
		if (isDebug) log.info("Plugin trace enabled.");
		
		this.traceMaterial = new HashMap<Player, Material>();
		this.tracePoints = new HashMap<Player, Map<String, Location>>();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
	}
	 
	public void onDisable(){ 
		if (isDebug) log.info("Plugin trace disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			log.info("Trace commands accepted from players only.");
			return false;
		}
		if (this.tracePoints.get(player) == null) {
			this.tracePoints.put(player, new HashMap<String, Location>());
		}
		// If the player typed /p1 then do the following...
		///trace [on||off] [material_id||material_name]
		if(cmd.getName().equalsIgnoreCase("trace")){ 
			if (isDebug) log.info("Got a trace command.");
			boolean isOn = true;
			if (args.length > 0) {
				if (!args[0].equals("on") && !args[0].equals("off")) {
					log.info("Unknown argument: '" + args[0] + "'");
					args[0] = "on";
				}
				isOn = args[0].equals("on");
			} 
			Material material = Material.RAILS;
			if (args.length > 1) {
				material = Material.matchMaterial(args[1]);
				if (material == null && isOn) {
					log.info("Unknown material: " + args[1] + " use RAILS");			
				} else {
					if (isDebug) log.info("Known material: " + material.name());
				}
			}
			if (isOn) {
				this.traceMaterial.put(player, material);
			} else {
				this.traceMaterial.put(player, null);
			}
			return true;
		} 
		if(cmd.getName().equalsIgnoreCase("pset")){ 
			if (isDebug) log.info("Got a pset command.");
			if (args.length > 0) {
				if (isDebug) log.info("Set a point: " + args[0]);
				this.tracePoints.get(player).put(args[0], player.getLocation());
				return true;
			}
			return false;
		} 
		if(cmd.getName().equalsIgnoreCase("pgo")){ 
			if (isDebug) log.info("Got a pgo command.");
			if (args.length > 0) {
				if (this.tracePoints.get(player).containsKey(args[0])) {
					if (isDebug) log.info("Go to the point: " + args[0]);
					player.teleport(this.tracePoints.get(player).get(args[0]));
					return true;
				}
			}			
			return false;
		} 
		//If this has happened the function will break and return true. 
		//if this hasn't happened the a value of false will be returned.
		return false; 
	}
	
	public boolean isTraceEnabled(Player player) {
		return this.traceMaterial.get(player) != null;
	}
	
	public boolean isTraceDebug() {
		return this.isDebug;
	}
	
	public Material getMaterial(Player player) {
		return this.traceMaterial.get(player);
	}
}
