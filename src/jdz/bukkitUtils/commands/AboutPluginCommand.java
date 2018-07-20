
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.misc.StringUtils;

@CommandLabel("about")
@CommandShortDescription("Gives information about the plugin")
public class AboutPluginCommand extends SubCommand {
	private static final ChatColor titleColor = ChatColor.GOLD;
	private static final ChatColor fieldColor = ChatColor.GREEN;
	private static final ChatColor descColor = ChatColor.GREEN;
	private static final ChatColor versionColor = ChatColor.YELLOW;
	private static final ChatColor authorColor = ChatColor.YELLOW;

	private final String[] messages;

	private String[] permissions = new String[0];

	public AboutPluginCommand(Plugin plugin) {
		this(plugin.getDescription().getName(), plugin.getDescription().getDescription(),
				plugin.getDescription().getVersion(), plugin.getDescription().getAuthors());
	}

	public AboutPluginCommand(String fullName, String description, String version, Collection<String> authors) {
		List<String> lines = new ArrayList<String>();

		lines.add(ChatColor.GRAY + "============[ " + titleColor + fullName + ChatColor.GRAY + " ]============");
		if (!description.equals(""))
			lines.add(descColor + description);
		if (!version.equals(""))
			lines.add(fieldColor + "Version: " + versionColor + version);
		if (!authors.isEmpty())
			if (authors.size() == 1)
				lines.add(fieldColor + "Author: " + authorColor + authors.iterator().next());
			else
				lines.add(fieldColor + "Authors: " + StringUtils.collectionToString(authors, ", "));
		lines.add(ChatColor.GRAY + StringUtils.repeat("=", lines.get(0).length() - 8));

		messages = lines.toArray(new String[lines.size()]);

	}

	@Override
	public void execute(CommandSender sender, String... args) {
		for (String permission : permissions)
			if (!sender.hasPermission(permission)) {
				sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that!");
				return;
			}
		sender.sendMessage(messages);
	}

	public void setPermissions(String... permissions) {
		this.permissions = permissions;
	}

}
