
package jdz.bukkitUtils.pluginUpdaters;

import org.bukkit.plugin.Plugin;

public class Version {
	private final String versionName;
	private final int[] versionNumbers;

	public Version(Plugin plugin) {
		this(plugin.getDescription().getVersion());
	}

	public Version(String versionName) {
		this.versionName = versionName;

		String[] args = versionName.split(".|,|-| ");
		versionNumbers = new int[args.length];
		for (int i = 0; i < args.length; i++)
			try {
				versionNumbers[i] = Integer.parseInt(args[i].replaceAll("\\D+", ""));
			}
			catch (NumberFormatException e) {
				versionNumbers[i] = 0;
			}
	}

	public boolean isNewerThan(Version other) {
		if (versionNumbers.length == 0)
			return false;

		for (int i = 0; i < versionNumbers.length; i++) {
			if (other.versionNumbers.length < i)
				return true;
			if (versionNumbers[i] > other.versionNumbers[i])
				return true;
			if (versionNumbers[i] < other.versionNumbers[i])
				return false;
		}
		return false;
	}

	@Override
	public String toString() {
		return versionName;
	}
}
