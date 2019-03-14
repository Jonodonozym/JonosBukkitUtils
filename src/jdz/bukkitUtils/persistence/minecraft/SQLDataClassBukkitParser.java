
package jdz.bukkitUtils.persistence.minecraft;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.persistence.ORM.SQLDataSerialiser;
import jdz.bukkitUtils.utils.WorldUtils;

public class SQLDataClassBukkitParser {

	public static void initDefaults() {
		SQLDataSerialiser.addParserSerialiser(OfflinePlayer.class, (uuid) -> {
			return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
		}, (player) -> {
			return player.getUniqueId().toString();
		});

		SQLDataSerialiser.addParserSerialiser(Player.class, (uuid) -> {
			return Bukkit.getPlayer(UUID.fromString(uuid));
		}, (player) -> {
			return player.getUniqueId().toString();
		});

		SQLDataSerialiser.addParserSerialiser(Location.class, (loc) -> {
			return WorldUtils.locationFromString(loc);
		}, (loc) -> {
			return WorldUtils.locationToString(loc);
		});

		SQLDataSerialiser.addParserSerialiser(World.class, (name) -> {
			return Bukkit.getWorld(name);
		}, (world) -> {
			return world.getName();
		});

		SQLDataSerialiser.addParserSerialiser(Chunk.class, (chunk) -> {
			return WorldUtils.chunkFromString(chunk);
		}, (chunk) -> {
			return WorldUtils.chunkToString(chunk);
		});

		SQLDataSerialiser.addParser(UUID.class, (s) -> {
			return UUID.fromString(s);
		});
		
		SQLDataSerialiser.addParserSerialiser(Material.class, (string) -> {
			return Material.valueOf(string);
		}, (material) -> {
			return material.name();
		});
	}

}
