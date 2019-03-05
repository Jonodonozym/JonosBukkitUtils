/**
 * FileExporter.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.fileIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;

/**
 * Lets you export files that you include in your plugin's .jar
 *
 * @author Jonodonozym
 */
public final class FileExporter {
	static boolean RUNNING_FROM_JAR = false;
	private final Plugin plugin;

	public FileExporter(Plugin plugin) {
		this.plugin = plugin;
	}

	private void ExportResource(String resourceName, String destinationPath, boolean isSilent) {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		try {
			stream = plugin.getClass().getResourceAsStream("/" + resourceName);
			if (stream == null)
				throw new Exception("Cannot get resource " + resourceName + " from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(destinationPath);
			while ((readBytes = stream.read(buffer)) > 0)
				resStreamOut.write(buffer, 0, readBytes);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			if (!isSilent)
				new FileLogger(JonosBukkitUtils.getInstance()).createErrorLog(ex);
		}
		finally {
			try {
				stream.close();
				resStreamOut.close();
			}
			catch (Exception ex2) {
				ex2.printStackTrace();
			}
		}
	}

	public boolean hasResource(String fileName) {
		return plugin.getClass().getResourceAsStream("/" + fileName) != null;
	}

	public void ExportResourceSilent(String resourceName, String destinationPath) {
		ExportResource(resourceName, destinationPath, true);
	}

	public void ExportResource(String resourceName, String destinationPath) {
		ExportResource(resourceName, destinationPath, false);
	}

	public void ExportResource(String resourceName) {
		String location = plugin.getDataFolder().getParentFile().getPath() + File.separator + resourceName;
		ExportResource(resourceName, location, false);
	}

	public JarFile getRunningJar() throws IOException {
		if (!RUNNING_FROM_JAR)
			return null; // null if not running from jar
		String path = new File(FileExporter.class.getProtectionDomain().getCodeSource().getLocation().getPath())
				.getAbsolutePath();
		path = URLDecoder.decode(path, "UTF-8");
		return new JarFile(path);
	}

	static {
		final URL resource = FileExporter.class.getClassLoader().getResource("/" + "plugin.yml");
		if (resource != null)
			RUNNING_FROM_JAR = true;
	}
}
