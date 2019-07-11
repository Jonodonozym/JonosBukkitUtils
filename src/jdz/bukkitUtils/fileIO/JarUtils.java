/**
 * JarUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 *
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.fileIO;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Class for extracting libs that your BukkitJUtils.plugin uses into the plugins
 * directory
 *
 * @author Jaiden Baker
 */
public final class JarUtils {
	private final Plugin plugin;
	private final FileExporter fileExporter;

	public JarUtils(Plugin plugin) {
		this.plugin = plugin;
		fileExporter = new FileExporter(plugin);
	}

	public void extractLibs(String... libNames) {
		try {
			for (final String libName : libNames) {
				File lib = getExportLocation(libName);
				if (!lib.exists())
					extractFromJar("libs" + libName, lib.getAbsolutePath());
			}

			loadLibs(libNames);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private boolean extractFromJar(final String fileName, final String dest) throws IOException {
		if (fileExporter.getRunningJar() == null)
			return false;
		fileExporter.ExportResource(fileName, dest);
		return true;
	}

	private void loadLibs(String... libNames) throws IOException {
		for (final String libName : libNames) {
			File lib = getExportLocation(libName);
			if (!lib.exists()) {
				String errorMessage = "There was a critical error loading " + plugin.getName()
						+ "! Could not find lib: " + lib.getAbsolutePath()
						+ ". If the problem persists, add it manually to your plugins directory";
				Bukkit.getLogger().warning(errorMessage);
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
			addClassPath(getJarUrl(lib));
		}
	}

	private File getExportLocation(String libName) {
		File libFolder = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile(), "Libs");
		if (!libFolder.exists())
			libFolder.mkdirs();

		File lib = new File(libFolder, libName);
		if (libName.contains("/"))
			lib = new File(libFolder, libName.substring(libName.lastIndexOf("/") + 1));

		return lib;
	}

	private void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = new URLClassLoader(new URL[] { url }, ClassLoader.getSystemClassLoader());
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		}
		catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url + " to system classloader");
		}
	}

	private URL getJarUrl(final File file) throws IOException {
		return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
	}

}