
package jdz.bukkitUtils.commands;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;

/**
 * Special command executor class where sub-commands are root commands as
 * opposed to commands with a common label
 *
 * @author Jaiden Baker
 */
public abstract class RootCommandExecutor extends CommandExecutor {

	public RootCommandExecutor(JavaPlugin plugin) {
		this(plugin, false);
	}

	public RootCommandExecutor(JavaPlugin plugin, boolean logCommands) {
		super(plugin, "", logCommands);
	}

	@Override
	public void addCommand(SubCommand c, JavaPlugin plugin) {
		register(Arrays.asList(c));
		super.addCommand(c, plugin);
	}

	@Override
	public void register() {
		register(getSubCommands());
	}

	private void register(Collection<SubCommand> commands) {
		for (SubCommand command : commands)
			for (String label : command.getLabels()) {

				if (plugin.getCommand(label) == null) {
					new FileLogger(plugin).createErrorLog(new IllegalArgumentException(),
							"No command found in " + plugin.getName() + "'s plugin.yml file labeled '" + label + "'");
					continue;
				}
				if (plugin.getCommand(label).getExecutor() == null)
					plugin.getCommand(label).setExecutor(this);
			}
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		String[] newArgs = new String[args.length + 1];
		newArgs[0] = label;
		for (int i = 1; i < args.length + 1; i++)
			newArgs[i] = args[i - 1];
		return super.onCommand(sender, cmd, "", newArgs);
	}
}
