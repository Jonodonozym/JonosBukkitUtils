
package jdz.bukkitUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Command extends SubCommand {
	
	public void register(JavaPlugin plugin) {
		
		CommandExecutor executor = new CommandExecutor(plugin, getLabel(), true) {
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
