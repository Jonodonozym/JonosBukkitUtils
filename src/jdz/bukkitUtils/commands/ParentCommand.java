
package jdz.bukkitUtils.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;

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
	public final void setDefaultCommand(SubCommand command) {executor.setDefaultCommand(command); }
	
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
	}
}
