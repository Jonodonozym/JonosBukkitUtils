package jdz.bukkitUtils.worldedit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.world.DataException;

@SuppressWarnings("deprecation")
public class Schematics {
	private final Map<String, CuboidClipboard> schemas = new HashMap<>();

	public CuboidClipboard getSchema(String name) {
		return schemas.get(name);
	}

	public Schematics(Plugin plugin) {
		loadSchematics(plugin.getDataFolder());
		System.out.println("[" + plugin.getName() + "] " + schemas.size() + " schematics loaded");
	}

	private void loadSchematics(File root) {
		File[] files = root.listFiles();
		if (files != null) {
			Bukkit.getLogger().info(files.length + " schematic files found.");
			for (File file : files)
				if (file.isDirectory())
					loadSchematics(file);
				else if (file.getName().endsWith(".schematic"))
					try {
						CuboidClipboard cc = CuboidClipboard.loadSchematic(file);
						schemas.put(file.getName().replace(".schematic", ""), cc);
					}
					catch (IOException | DataException e) {
						Bukkit.getLogger().info("Error loading schematic: " + e);
					}
		}
	}
}
