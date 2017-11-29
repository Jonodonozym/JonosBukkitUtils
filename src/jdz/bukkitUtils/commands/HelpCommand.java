
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandNoHelp;
import jdz.bukkitUtils.misc.StringUtils;

@CommandLabel("help")
@CommandLabel("?")
@CommandNoHelp
public final class HelpCommand extends SubCommand {
	private final CommandExecutor executor;

	private String[] permissions = new String[0];

	private ChatColor titleColor, usageColor, descColor;
	private int linesPerPage = 10;

	private List<String> extraMessages = new ArrayList<String>();
	private List<String> messages = null;
	
	private int numPages = 1;

	public HelpCommand(CommandExecutor executor) {
		this(executor, ChatColor.GOLD, ChatColor.GREEN, ChatColor.WHITE);
	}

	public HelpCommand(CommandExecutor executor, ChatColor titleColor, ChatColor usageColor, ChatColor descColor) {
		this.executor = executor;
		this.titleColor = titleColor;
		this.usageColor = usageColor;
		this.descColor = descColor;
	}

	public void setPermissions(String... permissions) {
		this.permissions = permissions;
	}

	public void setTitleColor(ChatColor color) {
		this.titleColor = color;
	}

	public void setUsageColor(ChatColor color) {
		this.usageColor = color;
		reload();
	}

	public void setDescriptionColor(ChatColor color) {
		this.descColor = color;
		reload();
	}
	
	public void addExtraMessage(String message) {
		extraMessages.add(message);
		reload();
	}

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (messages == null)
			reload();
		
		for (String permission : permissions)
			if (!sender.hasPermission(permission)) {
				sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that!");
				return;
			}
		try {
			int i = Integer.parseInt(args[0]);
			showPage(sender, i+1);
		} catch (Exception e) {
			
			if (args.length > 0)
				for(SubCommand command: executor.getSubCommands())
					if (command.getClass().getAnnotation(CommandNoHelp.class) == null)
						if (command.labelMatches(args[0])) 
							if (!command.getClass().equals(HelpCommand.class)) {
							sender.sendMessage(getCommandDesc(command, false));
							return;
						}
			
			showPage(sender, 0);
		}
	}

	private void reload() {
		if (messages == null)
			messages = new ArrayList<String>();
		messages.clear();
		
		for (SubCommand command : executor.getSubCommands()) {
			Class<? extends SubCommand> c = command.getClass();

			if (c.equals(getClass()))
				continue;
			if (c.getAnnotation(CommandNoHelp.class) != null)
				continue;

			messages.add(getCommandDesc(command, true));
		}

		numPages = (extraMessages.size() + messages.size() + linesPerPage - 1) / linesPerPage;
	}
	
	public String getCommandDesc(SubCommand command, boolean isShort) {
		String usage = command.getUsage();
		String description = isShort?command.getShortDescription():command.getLongDescription();
		if (description.equals(""))
			description = command.getShortDescription();
		
		String returnValue = usageColor.toString();
		if (usage.equals(""))
			returnValue += "/" + executor.getLabel() + " " + command.getLabel();
		else
			returnValue += usage;
		
		returnValue += descColor;
		if (!description.equals(""))
			returnValue += " - "+description;
		
		return returnValue;
	}

	public void showPage(CommandSender sender, int pageNumber) {
		if (pageNumber < 0)
			pageNumber = 0;
		if (pageNumber >= numPages)
			pageNumber = numPages-1;

		int numLines = Math.min(linesPerPage, messages.size() - pageNumber*linesPerPage);
		
		String[] lines = new String[numLines + 2];
		lines[0] = ChatColor.GRAY + "============[ " + titleColor + executor.getLabel() + " Help"
				+ (numPages <= 1 ? "" : (" " + (1+pageNumber) + "/" + numPages)) + ChatColor.GRAY + " ]============";
		
		lines[numLines + 1] = ChatColor.GRAY + StringUtils.repeat("=", lines[0].length() - 8);
		
		for (int i = 0; i < numLines; i++)
			lines[i + 1] = messages.get(pageNumber*linesPerPage+i);

		sender.sendMessage(lines);
	}

}
