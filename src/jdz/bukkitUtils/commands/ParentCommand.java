
package jdz.bukkitUtils.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

@CommandRequiredArgs(1)
/**
 * Command that acts as a parent for more children commands
 * e.g. /plot rent <sub-command>, where rent's sub-commands are: pay, list, payall, and
 * are defined as if you were doing /plot pay, /plot list etc.
 *
 * @author Jaiden Baker
 */
public abstract class ParentCommand extends SubCommand{
	private final ParentCommandExecutor executor = new ParentCommandExecutor(this);

	@Override
	public final boolean execute(CommandSender sender, String... args) {
		String[] subArgs = new String[args.length-1];
		for (int i=0; i<subArgs.length; i++)
			subArgs[i] = args[i+1];
		return executor.onCommand(sender, null, args[0], subArgs);
	}
	
	protected abstract List<SubCommand> getSubCommands();
	protected abstract SubCommand getDefaultCommand();
	
	private final class ParentCommandExecutor extends CommandExecutor {
		private final ParentCommand command;
		public ParentCommandExecutor(ParentCommand command) {
			this.command = command;
		}
		
		@Override
		protected List<SubCommand> getSubCommands() {
			return command.getSubCommands();
		}

		@Override
		protected SubCommand getDefaultCommand() {
			return command.getDefaultCommand();
		}
	}
}
