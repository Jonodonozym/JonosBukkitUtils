
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Command extends SubCommand {
	private boolean isRegistered = false;

	public void register(JavaPlugin plugin) {
		register(plugin, false);
	}

	public void register(JavaPlugin plugin, boolean doLogging) {
		if (isRegistered) {
			plugin.getLogger().warning(getLabel() + " command already registered!");

			StackTraceElement[] elements = new Exception().getStackTrace();
			for (int i = 1; i < Math.min(elements.length, 4); i++)
				plugin.getLogger().warning(elements[i].toString());

			return;
		}

		isRegistered = true;

		CommandExecutor executor = new CommandExecutor(plugin, getLabel(), doLogging) {
			@Override
			protected List<SubCommand> getSubCommands() {
				return new ArrayList<SubCommand>();
			}
		};

		executor.disableHelpCommand();
		executor.setDefaultCommand(this);
		executor.register();
	}
}
