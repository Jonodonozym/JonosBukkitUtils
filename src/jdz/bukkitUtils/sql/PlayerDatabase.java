
package jdz.bukkitUtils.sql;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PlayerDatabase extends Database implements Listener {
	public PlayerDatabase(JavaPlugin plugin) {
		super(plugin);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		api.runOnConnect(()->{setupTables();});
	}
	
	public abstract void setupTables();
	public abstract void addPlayer(Player player);
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		addPlayer(event.getPlayer());
	}
}
