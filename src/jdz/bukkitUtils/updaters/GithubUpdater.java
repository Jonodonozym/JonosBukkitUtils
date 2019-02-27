
package jdz.bukkitUtils.updaters;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import lombok.Getter;

public class GithubUpdater extends PluginDownloader {
	@Getter private final Plugin plugin;
	@Getter private final PluginUpdateType type;
	@Getter private final PluginUpdateResult result = PluginUpdateResult.DISABLED;

	private final Thread dataFetchThread;

	private Version latestVersion;
	private URL versionURL;

	public GithubUpdater(Plugin plugin, PluginUpdateType type, String repository, String login,
			String oauthAccessToken) {
		this.plugin = plugin;
		this.type = type;

		dataFetchThread = new Thread(() -> {
			try {
				fetchData(repository, login, oauthAccessToken);
			}
			catch (Exception e) {
				e.printStackTrace();
				latestVersion = new Version(plugin);
			}
		});
		dataFetchThread.start();
	}

	private void fetchData(String repoName, String login, String oauthAccessToken) throws Exception {
		System.out.println("Connecting to github");
		GitHub github = GitHub.connect(login, oauthAccessToken);
		System.out.println("Connecting to repo " + repoName);
		GHRepository repo = github.getRepository(repoName);
		System.out.println("fetching latest release");
		GHRelease latestRelease = repo.getLatestRelease();
		System.out.println("parsing version");
		latestVersion = new Version(latestRelease.getName());
		System.out.println("fetching download URL");
		versionURL = latestRelease.getAssets().get(0).getUrl();
		System.out.println("Connecting to github complete!");
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
		waitForDataFetch();
		return download(versionURL, targetFolder);
	}

	private void waitForDataFetch() {
		if (dataFetchThread != null && dataFetchThread.isAlive())
			try {
				dataFetchThread.join();
			}
			catch (final InterruptedException e) {
				plugin.getLogger().log(Level.SEVERE, null, e);
			}
	}

}
