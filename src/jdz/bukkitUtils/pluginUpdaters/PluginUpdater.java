
package jdz.bukkitUtils.pluginUpdaters;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.common.io.Files;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.configuration.Config;
import jdz.bukkitUtils.fileIO.FileLogger;

public class PluginUpdater {
	private static final File PLUGIN_UPDATE_DIR = new File(JonosBukkitUtils.getInstance().getDataFolder(),
			"scheduledUpdates");
	private static final File PLUGIN_DIR = JonosBukkitUtils.getInstance().getDataFolder().getParentFile();
	private static FileLogger logger = new FileLogger(JonosBukkitUtils.getInstance());

	static {
		if (!PLUGIN_UPDATE_DIR.exists())
			PLUGIN_UPDATE_DIR.mkdirs();
	}

	public static void update(PluginDownloader downloader) {
		new Thread(() -> {
			Plugin plugin = downloader.getPlugin();
			Version currentVersion = new Version(plugin);
			if (updatesEnabled(downloader.getPlugin()) && downloader.updateToLatestVersion()
					&& downloader.getLatestVersion().isNewerThan(currentVersion))
				try {
					File file = downloader.download(PLUGIN_UPDATE_DIR);
					if (file != null && PluginUnzipper.isZipFile(file))
						PluginUnzipper.unzip(file, PLUGIN_UPDATE_DIR);
					for (UpdateListener listener : downloader.getListeners())
						listener.onUpdate(downloader.getResult());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		}).run();;
	}

	public static boolean updatesEnabled(Plugin plugin) {
		FileConfiguration config = plugin.getConfig();
		if (config == null)
			config = Config.getConfig(plugin);
		if (!config.contains("autoupdate")) {
			config.set("autoUpdate", false);
			try {
				config.save(Config.getConfigFile(plugin));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return config.getBoolean("autoUpdate");
	}

	public static void updateAll() {
		for (File file : PLUGIN_UPDATE_DIR.listFiles()) {
			File pluginFile = new File(PLUGIN_DIR, file.getName());
			try {
				Files.copy(file, pluginFile);
				file.delete();
				JonosBukkitUtils.getInstance().getLogger().info(pluginFile.getName() + " Updated!");
			}
			catch (IOException e) {
				logger.createErrorLog(e);
			}
		}
	}

	public static File getUpdateJarFile(Plugin plugin) throws Exception {
		String jarName = getJarName(plugin);
		return new File(PLUGIN_UPDATE_DIR, jarName);
	}

	public static File getRunningJarFile(Plugin plugin) throws Exception {
		Class<?> pluginClass = plugin.getClass();
		Method method = pluginClass.getDeclaredMethod("getFile");
		method.setAccessible(true);
		File file = (File) method.invoke(plugin);
		return file;
	}

	private static String getJarName(Plugin plugin) throws Exception {
		return getRunningJarFile(plugin).getName();
	}
}
