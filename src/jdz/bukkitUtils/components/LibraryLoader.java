/**
 * JarUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.components;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.fileIO.FileExporter;

/**
 * Class for extracting libs that your BukkitJUtils.plugin uses into the plugins
 * directory
 *
 * @author Jaiden Baker
 */
public final class LibraryLoader {
	private static final Method addURLMethod = getAddURLMethod();

	private static Method getAddURLMethod() {
		try {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			return method;
		}
		catch (ReflectiveOperationException e) {
			Bukkit.getLogger().severe(e.getMessage());
			return null;
		}
	}
	
	public static void extractAndLoadLibraries(Plugin plugin, String... libraryNames) {
		FileExporter fileExporter = new FileExporter(plugin);
		try {
			for (final String libraryName : libraryNames)
				extractAndLoadLibrary(fileExporter, libraryName);
		}
		catch (final Exception e) {
			Bukkit.getLogger().severe(e.getMessage());
			Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	private static void extractAndLoadLibrary(FileExporter fileExporter, String libraryName) throws IOException, ReflectiveOperationException {
		File library = getExportLocation(fileExporter.getPlugin(), libraryName);
		if (!library.exists())
			fileExporter.ExportResource("libs/" + libraryName, library.getAbsolutePath());
		loadLibrary(fileExporter.getPlugin(), library);
	}

	private static void loadLibrary(Plugin plugin, File library) throws IOException, ReflectiveOperationException {
		if (!library.exists())
			throw new IOException("There was a critical error loading " + plugin.getName()
					+ "! Could not find library at " + library.getAbsolutePath()
					+ ". If the problem persists, manually extract the library from the plugin jar and place it in the correct location");
		addLibraryToClasspath(library);
	}

	private static File getExportLocation(Plugin plugin, String libraryName) {
		File libraryDirectory = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile(), "Libs");
		if (!libraryDirectory.exists())
			libraryDirectory.mkdirs();

		File library = new File(libraryDirectory, libraryName);
		if (libraryName.contains("/"))
			library = new File(libraryDirectory, libraryName.substring(libraryName.lastIndexOf("/") + 1));

		return library;
	}

	private static void addLibraryToClasspath(File library) throws IOException, ReflectiveOperationException {
		if (addURLMethod == null)
			return;

		URL url = new URL("jar:" + library.toURI().toURL().toExternalForm() + "!/");
		URLClassLoader classLoader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());
		addURLMethod.invoke(classLoader, new Object[] { url });
	}

}