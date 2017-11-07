
package jdz.jbu.commands;

import org.bukkit.command.CommandSender;

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
	
	public String usage() {
		return this.getClass().getAnnotation(CommandUsage.class).value();
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
