
package jdz.bukkitUtils.pluginUpdaters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

public abstract class PluginDownloader {
	private static final int BYTE_SIZE = 1024;

	@Getter private final List<UpdateListener> listeners = new ArrayList<>();
	protected PluginUpdateResult result = PluginUpdateResult.NO_UPDATE;

	public void addListener(UpdateListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener(UpdateListener listener) {
		return listeners.remove(listener);
	}

	public abstract Version getLatestVersion();

	abstract boolean updateToLatestVersion();

	abstract File download(File targetFolder);

	abstract Plugin getPlugin();

	abstract PluginUpdateType getType();

	abstract PluginUpdateResult getResult();

	public void updatePlugin() {
		PluginUpdater.update(this);
	}

	protected File download(URL fileURL, File targetFolder) {
		URL finalURL;
		try {
			finalURL = followRedirects(fileURL);
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(finalURL.openStream());
			String fileName = Paths.get(finalURL.toURI().getPath()).getFileName().toString();
			File file = new File(targetFolder, fileName);
			fout = new FileOutputStream(file);

			final byte[] data = new byte[BYTE_SIZE];
			int count;
			while ((count = in.read(data, 0, BYTE_SIZE)) != -1)
				fout.write(data, 0, count);
			return file;
		}
		catch (Exception ex) {
			Bukkit.getLogger().log(Level.WARNING,
					"The auto-updater tried to download a new update, but was unsuccessful.", ex);
			result = PluginUpdateResult.FAIL_DOWNLOAD;
		}
		finally {
			try {
				if (in != null)
					in.close();
				if (fout != null)
					fout.close();
			}
			catch (final IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	private URL followRedirects(URL location) throws IOException {
		URL resourceUrl, base, next;
		HttpURLConnection conn;
		String redLoc;
		while (true) {
			resourceUrl = location;
			conn = (HttpURLConnection) resourceUrl.openConnection();

			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0...");

			switch (conn.getResponseCode()) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				redLoc = conn.getHeaderField("Location");
				base = location;
				next = new URL(base, redLoc);
				location = next;
				continue;
			}
			break;
		}
		return conn.getURL();
	}
}
