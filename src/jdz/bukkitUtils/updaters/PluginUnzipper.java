
package jdz.bukkitUtils.updaters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;

public class PluginUnzipper {
	private static final int BYTE_SIZE = 1024;

	public static boolean isZipFile(File file) {
		return file.getName().endsWith(".zip");
	}

	public static boolean unzip(File fSourceZip, File jarFolder) {
		try {
			final String zipPath = fSourceZip.getPath().substring(0, fSourceZip.getPath().length() - 4);
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());
				fileIOOrError(destinationFilePath.getParentFile(), destinationFilePath.getParentFile().mkdirs(), true);

				if (!entry.isDirectory()) {
					final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
					int b;
					final byte[] buffer = new byte[BYTE_SIZE];
					final FileOutputStream fos = new FileOutputStream(destinationFilePath);
					final BufferedOutputStream bos = new BufferedOutputStream(fos, BYTE_SIZE);

					while ((b = bis.read(buffer, 0, BYTE_SIZE)) != -1)
						bos.write(buffer, 0, b);

					bos.flush();
					bos.close();
					bis.close();

					final String name = destinationFilePath.getName();

					if (name.endsWith(".jar") && pluginExists(name)) {
						File output = new File(jarFolder, name);
						fileIOOrError(output, destinationFilePath.renameTo(output), true);
					}
				}
			}
			zipFile.close();

			// Move any plugin data folders that were included to the right place, Bukkit
			// won't do this for us.
			moveUnzippedDataFiles(zipPath);
			return true;
		}
		catch (final IOException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
			return false;
		}
		finally {
			fileIOOrError(fSourceZip, fSourceZip.delete(), false);
		}
	}

	private static void moveUnzippedDataFiles(String zipPath) {
		File[] list = listFilesOrError(new File(zipPath));
		for (final File dFile : list) {
			if (dFile.isDirectory() && pluginExists(dFile.getName())) {
				Plugin plugin = Bukkit.getPluginManager().getPlugin(dFile.getName());
				final File oFile = new File(plugin.getDataFolder().getParent(), dFile.getName());
				final File[] dList = listFilesOrError(dFile);
				final File[] oList = listFilesOrError(oFile);
				for (File cFile : dList) {
					boolean found = false;
					for (final File xFile : oList)
						if (xFile.getName().equals(cFile.getName())) {
							found = true;
							break;
						}
					if (!found) {
						File output = new File(oFile, cFile.getName());
						fileIOOrError(output, cFile.renameTo(output), true);
					}
					else
						fileIOOrError(cFile, cFile.delete(), false);
				}
			}
			fileIOOrError(dFile, dFile.delete(), false);
		}
		File zip = new File(zipPath);
		fileIOOrError(zip, zip.delete(), false);
	}

	public static boolean pluginExists(String pluginName) {
		return getPluginFile(pluginName) != null;
	}

	public static File getPluginFile(String pluginName) {
		for (File file : JonosBukkitUtils.getInstance().getDataFolder().getParentFile().listFiles())
			if (file.getName().startsWith(pluginName))
				return file;
		return null;
	}

	private static void fileIOOrError(File file, boolean result, boolean create) {
		if (!result)
			Bukkit.getLogger().severe(
					"The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
	}

	private static File[] listFilesOrError(File folder) {
		File[] contents = folder.listFiles();
		if (contents == null) {
			Bukkit.getLogger().severe("The updater could not access files at: " + folder.getAbsolutePath());
			return new File[0];
		}
		else
			return contents;
	}
}
