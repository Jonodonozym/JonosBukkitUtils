
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandNoHelp;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.bukkitUtils.misc.StringUtils;

@CommandLabel("help")
@CommandLabel("?")
public class HelpCommand extends SubCommand {
	private final CommandExecutor executor;
	
	private ChatColor titleColor, usageColor, descColor;
	private int linesPerPage = 10;

	List<String> messages = new ArrayList<String>();
	private int numPages = 1;

	HelpCommand(CommandExecutor executor) {
		this(executor, ChatColor.GOLD, ChatColor.GREEN, ChatColor.WHITE);
	}
	
	HelpCommand(CommandExecutor executor, ChatColor titleColor, ChatColor usageColor, ChatColor descColor) {
		this.executor = executor;
		this.titleColor = titleColor;
		this.usageColor = usageColor;
		this.descColor = descColor;
		reload();
	}

	private void reload() {
		messages.clear();
		for (SubCommand command : executor.getSubCommands()) {
			Class<? extends SubCommand> c = command.getClass();

			if (c.equals(getClass()))
				continue;
			if (c.getAnnotation(CommandNoHelp.class) != null)
				continue;

			CommandUsage usage = c.getAnnotation(CommandUsage.class);

			String line = usageColor.toString();
			if (usage == null)
				line += "/" + executor.getLabel() + " " + command.getLabel();
			else
				line += command.getUsage();

			String desc = command.getDescription();
			if (!desc.equals(""))
				line += descColor + " - " + desc;

			messages.add(line);
		}
		
		numPages = (messages.size() + linesPerPage - 1) / linesPerPage;
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

	@Override
	public boolean execute(CommandSender sender, String... args) {
		try {
			int i = Integer.parseInt(args[0]);
			showPage(sender, i);
		}
		catch (Exception e) {
			showPage(sender, 0);
		}
		return true;
	}
	
	public void showPage(CommandSender sender, int pageNumber) {
		if (pageNumber < 1)
			pageNumber = 1;
		if (pageNumber > numPages)
			pageNumber = numPages;
			
		String[] lines = new String[linesPerPage+2];
		lines[0] = ChatColor.GRAY+"==========[ "+titleColor+executor.getLabel()+" Help"+ (numPages<=1?"":(" "+pageNumber + "/"+numPages))+ChatColor.GRAY +" ]==========";
		lines[linesPerPage+1] = ChatColor.GRAY + StringUtils.repeat("=", lines[0].length()-4);
		for (int i = pageNumber * linesPerPage; i < Math.min((pageNumber + 1) * linesPerPage, messages.size()); i++)
			lines[i - pageNumber * linesPerPage + 1] = messages.get(i);
		
		sender.sendMessage(lines);
	}

}
