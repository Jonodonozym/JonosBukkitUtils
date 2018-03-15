/**
 * JarUtils.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.fileIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.JonosBukkitUtils;

/**
 * Class for extracting libs that your BukkitJUtils.plugin uses into the plugins
 * directory
 *
 * @author Jaiden Baker
 */
public final class JarUtils {
	private final JavaPlugin plugin;
	private final FileExporter fileExporter;

	public JarUtils(JavaPlugin plugin) {
		this.plugin = plugin;
		fileExporter = new FileExporter(plugin);
	}

	public void extractLibs(String... libNames) {
		try {
			for (final String libName : libNames) {
				File lib = new File(plugin.getDataFolder(), libName);
				extractFromJar(lib.getName(), lib.getAbsolutePath());
			}
			for (final String libName : libNames) {
				File lib = new File(plugin.getDataFolder(), libName);
				if (!lib.exists()) {
					String errorMessage = "There was a critical error loading " + plugin.getName()
							+ "! Could not find lib: " + libName
							+ ". If the problem persists, add it manually to your plugins directory";
					Bukkit.getLogger().warning(errorMessage);
					new FileLogger(JonosBukkitUtils.getInstance()).createErrorLog(errorMessage);
					Bukkit.getServer().getPluginManager().disablePlugin(plugin);
					return;
				}
				addClassPath(getJarUrl(lib));
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
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

	private boolean extractFromJar(final String fileName, final String dest) throws IOException {
		if (fileExporter.getRunningJar() == null) {
			return false;
		}
		final File file = new File(dest);
		if (file.isDirectory()) {
			file.mkdir();
			return false;
		}
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		final JarFile jar = fileExporter.getRunningJar();
		final Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			final JarEntry je = e.nextElement();
			if (!je.getName().contains(fileName)) {
				continue;
			}
			final InputStream in = new BufferedInputStream(jar.getInputStream(je));
			final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			copyInputStream(in, out);
			jar.close();
			return true;
		}
		jar.close();
		return false;
	}

	private URL getJarUrl(final File file) throws IOException {
		return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
	}

	private final void copyInputStream(final InputStream in, final OutputStream out) throws IOException {
		try {
			final byte[] buff = new byte[4096];
			int n;
			while ((n = in.read(buff)) > 0) {
				out.write(buff, 0, n);
			}
		}
		finally {
			out.flush();
			out.close();
			in.close();
		}
	}

}