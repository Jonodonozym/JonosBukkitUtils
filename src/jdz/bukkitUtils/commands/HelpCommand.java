
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandNoHelp;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.bukkitUtils.utils.StringUtils;

@CommandLabel("help")
@CommandLabel("?")
@CommandNoHelp
public final class HelpCommand extends SubCommand {
	private final CommandExecutor executor;

	private ChatColor titleColor, usageColor, descColor;
	private int linesPerPage = 10;

	private List<String> extraMessages = new ArrayList<>();
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

	public void setTitleColor(ChatColor color) {
		titleColor = color;
	}

	public void setUsageColor(ChatColor color) {
		usageColor = color;
		reload();
	}

	public void setDescriptionColor(ChatColor color) {
		descColor = color;
		reload();
	}

	public void addExtraMessage(String message) {
		extraMessages.add(message);
		reload();
	}

	public void addExtraCommand(SubCommand command) {
		extraMessages.add(getCommandDesc(command, true));
		reload();
	}

	@CommandMethod
	public void execute(CommandSender sender) {
		execute(sender, 1);
	}

	@CommandMethod
	public void execute(CommandSender sender, int page) {
		if (messages == null)
			reload();

		showPage(sender, page - 1);
	}

	@CommandMethod
	public void execute(CommandSender sender, String commandName) {
		if (messages == null)
			reload();

		for (SubCommand command : executor.getSubCommands()) {
			if (command.getClass().getAnnotation(CommandNoHelp.class) != null)
				continue;

			if (command.labelMatches(commandName)) {
				if (command instanceof HelpCommand)
					continue;
				sender.sendMessage(getCommandDesc(command, false));
				return;
			}
		}
	}

	public void reload() {
		if (messages == null)
			messages = new ArrayList<>();
		messages.clear();

		CommandUsage usage = executor.getDefaultCommand().getClass().getAnnotation(CommandUsage.class);
		if (usage != null && executor.getDefaultCommand().getClass().getAnnotation(CommandNoHelp.class) == null)
			messages.add(getCommandDesc(executor.getDefaultCommand(), true));

		addMessages(executor);

		numPages = (extraMessages.size() + messages.size() + linesPerPage - 1) / linesPerPage;
	}

	private void addMessages(CommandExecutor executor) {
		for (SubCommand command : executor.getSubCommands()) {
			if (command.getClass().getAnnotation(CommandNoHelp.class) != null)
				continue;

			if (command instanceof ParentCommand)
				addMessages(((ParentCommand) command).getChildCommandExecutor());
			else
				messages.add(getCommandDesc(command, executor.getLabel(), true));
		}
	}

	public String getCommandDesc(SubCommand command, boolean isShort) {
		return getCommandDesc(command, executor.getLabel(), isShort);
	}

	public String getCommandDesc(SubCommand command, String executorLabel, boolean isShort) {
		String returnValue = usageColor + "/" + executorLabel + " " + command.getLabel();
		if (!command.getUsage().equals(""))
			returnValue += " " + command.getUsage();

		returnValue += descColor;
		String description = isShort ? command.getShortDescription() : command.getLongDescription();
		if (description.equals(""))
			description = command.getShortDescription();
		if (!description.equals(""))
			returnValue += " - " + description;

		return returnValue;
	}

	public void showPage(CommandSender sender, int pageNumber) {
		if (pageNumber < 0)
			pageNumber = 0;
		if (pageNumber >= numPages)
			pageNumber = numPages - 1;

		int numLines = Math.min(linesPerPage, messages.size() - pageNumber * linesPerPage);

		String[] lines = new String[numLines + 2];
		lines[0] = ChatColor.GRAY + "============[ " + titleColor + executor.getLabel() + " Help"
				+ (numPages <= 1 ? "" : " " + (1 + pageNumber) + "/" + numPages) + ChatColor.GRAY + " ]============";

		lines[numLines + 1] = ChatColor.GRAY + StringUtils.repeat("=", lines[0].length() - 8);

		for (int i = 0; i < numLines; i++)
			lines[i + 1] = messages.get(pageNumber * linesPerPage + i);

		sender.sendMessage(lines);
	}

}
