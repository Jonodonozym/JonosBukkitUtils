package jdz.bukkitUtils.updaters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.Getter;

/**
 * Check for updates on BukkitDev for a given plugin, and download the updates
 * if needed.
 * <p>
 * <b>VERY, VERY IMPORTANT</b>: Because there are no standards for adding
 * auto-update toggles in your plugin's config, this system provides NO CHECK
 * WITH YOUR CONFIG to make sure the user has allowed auto-updating. <br>
 * It is a <b>BUKKIT POLICY</b> that you include a boolean value in your config
 * that prevents the auto-updater from running <b>AT ALL</b>. <br>
 * If you fail to include this option in your config, your plugin will be
 * <b>REJECTED</b> when you attempt to submit it to dev.bukkit.org.
 * </p>
 * An example of a good configuration option would be something similar to
 * 'auto-update: true' - if this value is set to false you may NOT run the
 * auto-updater. <br>
 * If you are unsure about these rules, please read the plugin submission
 * guidelines: http://goo.gl/8iU5l
 *
 * @author Gravity
 * @version 2.4
 */

public class BukkitDevUpdater extends PluginDownloader {
	private static final String TITLE_VALUE = "name";
	private static final String LINK_VALUE = "downloadUrl";
	private static final String VERSION_VALUE = "gameVersion";

	private static final String QUERY = "/servermods/files?projectIds=";
	private static final String HOST = "https://api.curseforge.com";
	private static final String USER_AGENT = "Updater (by Gravity, modded by Jonodonozym)";
	private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" };

	// Plugin running Updater
	@Getter private final Plugin plugin;
	@Getter private final PluginUpdateType type;

	private int curseID = -1;
	private String BukkitAPIKey = null;

	private URL BukkitPluginURL;
	private Thread dataFetchThread;
	private PluginUpdateResult result = PluginUpdateResult.SUCCESS;

	private Version version;
	private String versionLink;
	private String versionGameVersion;

	public BukkitDevUpdater(Plugin plugin, int curseID, PluginUpdateType type, String APIKey) throws Exception {
		this.plugin = plugin;
		this.type = type;
		this.curseID = curseID;
		this.BukkitAPIKey = APIKey;
		this.version = new Version(plugin);

		try {
			this.BukkitPluginURL = new URL(BukkitDevUpdater.HOST + BukkitDevUpdater.QUERY + this.curseID);
		}
		catch (final MalformedURLException e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"The project ID provided for updating, " + this.curseID + " is invalid.", e);
			this.result = PluginUpdateResult.FAIL_BADID;
		}

		if (this.result != PluginUpdateResult.FAIL_BADID) {
			this.dataFetchThread = new Thread(() -> {
				fetchData();
			});
			this.dataFetchThread.start();
		}
		else {
			fetchData();
		}
	}

	@Override
	PluginUpdateResult getResult() {
		waitForDataFetch();
		return result;
	}

	@Override
	public Version getLatestVersion() {
		waitForDataFetch();
		return version;
	}

	public String getLatestGameVersion() {
		waitForDataFetch();
		return versionGameVersion;
	}

	public String getLatestFileLink() {
		waitForDataFetch();
		return versionLink;
	}

	@Override
	boolean updateToLatestVersion() {
		waitForDataFetch();
		for (final String string : BukkitDevUpdater.NO_UPDATE_TAG)
			if (version.toString().contains(string))
				return false;
		return true;
	}

	private void waitForDataFetch() {
		if ((dataFetchThread != null) && dataFetchThread.isAlive()) {
			try {
				dataFetchThread.join();
			}
			catch (final InterruptedException e) {
				plugin.getLogger().log(Level.SEVERE, null, e);
			}
		}
	}

	/**
	 * Make a connection to the BukkitDev API and request the newest file's details.
	 *
	 * @return true if successful.
	 */
	private boolean fetchData() {
		try {
			final URLConnection conn = this.BukkitPluginURL.openConnection();
			conn.setConnectTimeout(5000);

			if (this.BukkitAPIKey != null) {
				conn.addRequestProperty("X-API-Key", this.BukkitAPIKey);
			}
			conn.addRequestProperty("User-Agent", BukkitDevUpdater.USER_AGENT);

			conn.setDoOutput(true);

			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String response = reader.readLine();

			final JSONArray array = (JSONArray) JSONValue.parse(response);

			if (array.isEmpty()) {
				Bukkit.getLogger().warning("The updater could not find any files for the project id " + this.curseID);
				this.result = PluginUpdateResult.FAIL_BADID;
				return false;
			}

			JSONObject latestUpdate = (JSONObject) array.get(array.size() - 1);
			this.version = new Version((String) latestUpdate.get(BukkitDevUpdater.TITLE_VALUE));
			this.versionLink = (String) latestUpdate.get(BukkitDevUpdater.LINK_VALUE);
			this.versionGameVersion = (String) latestUpdate.get(BukkitDevUpdater.VERSION_VALUE);

			return true;
		}
		catch (final IOException e) {
			if (e.getMessage().contains("HTTP response code: 403")) {
				Bukkit.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
				Bukkit.getLogger().severe("Please double-check your configuration to ensure it is correct.");
				this.result = PluginUpdateResult.FAIL_APIKEY;
			}
			else {
				Bukkit.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
				Bukkit.getLogger().severe(
						"If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
				this.result = PluginUpdateResult.FAIL_CONNECTION;
			}
			Bukkit.getLogger().severe(e.getClass().getName());
			return false;
		}
	}

	@Override
	public File download(File targetFolder) {
		try {
			return (download(new URL(this.versionLink), targetFolder));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}