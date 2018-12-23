
package jdz.bukkitUtils.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;

/**
 * SubCommand that acts as a parent for more sub-commands
 * e.g. {/plot rent <sub-command>}, where rent's sub-commands are: pay, list,
 * payall
 *
 * @author Jaiden Baker
 */
@CommandRequiredArgs(1)
public abstract class ParentCommand extends SubCommand {
	private final ParentCommandExecutor childCommandExecutor = new ParentCommandExecutor(this);
	private final CommandExecutor commandExecutor;

	public ParentCommand(CommandExecutor commandExecutor) {
		this.commandExecutor = commandExecutor;
	}

	@Override
	public final void execute(CommandSender sender, String... args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Insufficient arguments");
			if (!getUsage().equals(""))
				sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
			return;
		}

		childCommandExecutor.onCommand(sender, null, commandExecutor.getLabel() + " " + getLabel(), args);
	}

	protected abstract List<SubCommand> getSubCommands();

	public final void setDefaultCommand(SubCommand command) {
		childCommandExecutor.setDefaultCommand(command);
	}

	private final class ParentCommandExecutor extends CommandExecutor {
		private final ParentCommand command;

		public ParentCommandExecutor(ParentCommand command) {
			super(null, command.getLabel(), false);
			this.command = command;
		}

		@Override
		protected List<SubCommand> getSubCommands() {
			return command.getSubCommands();
		}

		@Override
		public String getLabel() {
			return command.commandExecutor.getLabel() + " " + command.getLabel();
		}
	}
}
