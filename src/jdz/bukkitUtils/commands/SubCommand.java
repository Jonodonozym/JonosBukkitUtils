
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandLabels;
import jdz.bukkitUtils.commands.annotations.CommandLongDescription;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandPermissions;
import jdz.bukkitUtils.commands.annotations.CommandPlayerOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;

public abstract class SubCommand {
	private final List<String> labels;
	
	public SubCommand() {
		CommandLabels commandLabels = this.getClass().getAnnotation(CommandLabels.class);
		CommandLabel label = this.getClass().getAnnotation(CommandLabel.class);
		if (commandLabels == null && label == null)
			throw new RuntimeException(getClass().getName()+" command doesn't have the required @CommandLabel annotation!");

		labels = new ArrayList<String>(commandLabels==null?1:commandLabels.value().length);
		
		if (commandLabels != null)
			for(CommandLabel l: commandLabels.value())
				labels.add(l.value());
		else
			labels.add(label.value());
	}
	
	public boolean labelMatches(String label) {
		for (String s: labels)
			if (s.equalsIgnoreCase(label))
				return true;
		return false;
	}
	
	public boolean hasRequiredPermissions(CommandSender sender) {
		CommandPermissions perms = this.getClass().getAnnotation(CommandPermissions.class);
		if (perms == null)
			return true;

		for (CommandPermission perm: perms.value())
			if (!sender.hasPermission(perm.value()))
				return false;
		
		return true;
	}
	
	public int requiredArgs() {
		CommandRequiredArgs an = getClass().getAnnotation(CommandRequiredArgs.class);
		return an == null?0:an.value();
	}
	
	public String getShortDescription() {
		CommandShortDescription desc = getClass().getAnnotation(CommandShortDescription.class);
		return desc == null?"":desc.value();
	}
	
	public String getUsage() {
		CommandUsage usage = getClass().getAnnotation(CommandUsage.class);
		return usage == null?"":usage.value();
	}
	
	public String getLongDescription() {
		CommandLongDescription desc = getClass().getAnnotation(CommandLongDescription.class);
		return desc == null?getShortDescription():desc.value();
	}
	
	public String getLabel() {
		return labels.get(0);
	}
	
	public List<String> getLabels() {
		return labels;
	}
	
	public boolean isPlayerOnly() {
		return (this.getClass().getAnnotation(CommandPlayerOnly.class) != null);
	}

	public abstract void execute(CommandSender sender, Set<String> flags, String... args);
}
