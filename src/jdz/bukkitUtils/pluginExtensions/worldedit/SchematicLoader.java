package jdz.bukkitUtils.pluginExtensions.worldedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

public class SchematicLoader {
	private final Map<String, Clipboard> schemas = new HashMap<>();

	public Clipboard getSchema(String name) {
		return schemas.get(name);
	}

	public SchematicLoader(Plugin plugin) {
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
				else {
					ClipboardFormat format = ClipboardFormats.findByFile(file);
					if (format == null)
						continue;

					try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
						String schematicName = file.getName();
						if (schematicName.lastIndexOf(".") != -1)
							schematicName = schematicName.substring(0, file.getName().lastIndexOf(".") - 1);
						schemas.put(schematicName, reader.read());
					}
					catch (IOException e) {
						Bukkit.getLogger().info("Error loading schematic: " + e);
					}
				}
		}
	}
}
