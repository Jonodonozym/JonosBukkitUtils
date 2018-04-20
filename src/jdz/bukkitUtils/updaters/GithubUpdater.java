
package jdz.bukkitUtils.updaters;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import lombok.AccessLevel;
import lombok.Getter;

public class GithubUpdater extends PluginDownloader {
	@Getter(value = AccessLevel.PACKAGE) private final Plugin plugin;
	@Getter(value = AccessLevel.PACKAGE) private final PluginUpdateType type;
	@Getter(value = AccessLevel.PACKAGE) private final PluginUpdateResult result = PluginUpdateResult.DISABLED;

	private final Thread dataFetchThread;

	private Version latestVersion;
	private URL versionURL;

	public GithubUpdater(Plugin plugin, PluginUpdateType type, String repository, String login,
			String oauthAccessToken) {
		this.plugin = plugin;
		this.type = type;

		this.dataFetchThread = new Thread(() -> {
			try {
				fetchData(repository, login, oauthAccessToken);
			}
			catch (Exception e) {
				e.printStackTrace();
				latestVersion = new Version(plugin);
			}
		});
		this.dataFetchThread.start();
	}

	private void fetchData(String repoName, String login, String oauthAccessToken) throws Exception {
		GitHub github = GitHub.connect(login, oauthAccessToken);
		GHRepository repo = github.getRepository(repoName);
		GHRelease latestRelease = repo.getLatestRelease();
		latestVersion = new Version(latestRelease.getName());
		versionURL = latestRelease.getAssets().get(0).getUrl();
	}

	@Override
	public Version getLatestVersion() {
		waitForDataFetch();
		return latestVersion;
	}

	@Override
	boolean updateToLatestVersion() {
		return versionURL != null;
	}

	@Override
	File download(File targetFolder) {
		return download(versionURL, targetFolder);
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

}
