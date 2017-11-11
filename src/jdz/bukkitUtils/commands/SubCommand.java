
package jdz.bukkitUtils.commands;

import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandDescription;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandLabels;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandPlayerOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;

public abstract class SubCommand {
	public boolean labelMatches(String label) {
		CommandLabels commandLabels = this.getClass().getAnnotation(CommandLabels.class);
		for (CommandLabel commandLabel: commandLabels.value())
			if (commandLabel.value().equalsIgnoreCase(label))
				return true;
		return false;
	}
	
	public boolean hasRequiredPermissions(CommandSender sender) {
		CommandPermission perms = this.getClass().getAnnotation(CommandPermission.class);
		if (perms == null)
			return true;
		return sender.hasPermission(perms.value());
	}
	
	public int requiredArgs() {
		return this.getClass().getAnnotation(CommandRequiredArgs.class).value();
	}
	
	public String getDescription() {
		CommandDescription desc = this.getClass().getAnnotation(CommandDescription.class);
		if (desc == null) return "";
		return desc.value();
	}
	
	public String getUsage() {
		CommandUsage usage = this.getClass().getAnnotation(CommandUsage.class);
		if (usage == null) return "";
		return usage.value();
	}
	
	public String getLabel() {
		CommandLabels commandLabels = this.getClass().getAnnotation(CommandLabels.class);
		return commandLabels.value()[0].value();
	}
	
	public boolean isPlayerOnly() {
		return (this.getClass().getAnnotation(CommandPlayerOnly.class) != null);
	}

	public abstract boolean execute(CommandSender sender, String... args);
}
