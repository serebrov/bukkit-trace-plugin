package org.seb.trace;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TracePluginPlayerListener extends PlayerListener {

	public static TracePlugin plugin;
	
	Logger log = Logger.getLogger("Minecraft");
	
	public TracePluginPlayerListener(TracePlugin instance) {
		plugin = instance;
	}
	
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (plugin.isTraceEnabled(player)) {
			Location from = event.getFrom();	
			Location to = event.getTo();			
			if (from.getX() == to.getX()) {
				return;
			}
			
			Location loc = player.getLocation();
			World w = loc.getWorld();
			loc.setY(loc.getY() - this.getMaterialOffset(plugin.getMaterial(player)));
			Block b = w.getBlockAt(loc);
			if (b.getType().getId() != plugin.getMaterial(player).getId()) {
				if (plugin.isTraceDebug()) log.info("Change block type to " + plugin.getMaterial(player).name());
				b.setType(plugin.getMaterial(player));
			}
		}
	}
	
	private int getMaterialOffset(Material m) {
		if (m.isBlock() && m.getId() != Material.RAILS.getId()) {
			return 1;
		}
		return 0;
	}
	
}
