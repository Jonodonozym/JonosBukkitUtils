
package jdz.jbu.commands;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.jbu.fileIO.FileLogger;
import jdz.jbu.misc.StringUtils;

public abstract class CommandExecutor implements org.bukkit.command.CommandExecutor {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss");
	private final JavaPlugin plugin;
	private final boolean logCommands;
	private final String label;
	private final FileLogger fileLogger;
	
	public CommandExecutor() {
		this.logCommands = false;
		this.label = "";
		fileLogger = null;
		this.plugin = null;
	}
	
	public CommandExecutor(JavaPlugin plugin, String label, boolean logCommands) {
		this.logCommands = logCommands;
		this.label = label;
		this.plugin = plugin;
		fileLogger = new FileLogger(plugin, "CommandLogs");
	}
	
	private boolean registered = false;
	public void register() {
		if (!registered) {
			plugin.getCommand(label).setExecutor(this);
			registered = true;
		}
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		CommandExecutorPlayerOnly cepo = this.getClass().getAnnotation(CommandExecutorPlayerOnly.class);
		if (cepo != null && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED+"You must be a player to do that!");
			return true;
		}
		
		if (args.length == 0 && getDefaultCommand() != null) {
			execute(getDefaultCommand(), sender, args);
			return true;
		}
		
		for (SubCommand command : getSubCommands()) {
			if (command.labelMatches(args[1])) {
				String[] subArgs = new String[args.length - 2];
				for (int i = 0; i < subArgs.length; i++)
					subArgs[i] = args[i + 2];
				execute(command, sender, subArgs);
				return true;
			}
		}

		if (getDefaultCommand() != null) {
			logCommand(sender, label, args);
			return true;
		}
		
		return false;
	}
	
	public final void execute(SubCommand command, CommandSender sender, String...args) {
		if (command.requiredArgs() > args.length-2) {
			sender.sendMessage(ChatColor.RED+"Insufficient arguments");
			if (!command.usage().equals(""))
				sender.sendMessage(ChatColor.RED+"Usage: "+command.usage());
		}
		else if (command.isPlayerOnly() && !(sender instanceof Player))
			sender.sendMessage(ChatColor.RED+"You must be a player to do that!");
		else
			executeIfHasPerms(command, sender, args);
	}
	
	private final void executeIfHasPerms(SubCommand command, CommandSender sender, String...args) {
		logCommand(sender, command.getLabel(), args);
		if (getDefaultCommand().hasRequiredPermissions(sender))
			getDefaultCommand().execute(sender, args);
		else
			sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that");
	}	
	
	private final void logCommand(CommandSender sender, String label, String[] args) {
		if (logCommands)
			fileLogger.log("[" + dtf.format(new Date().toInstant()) + "] " + sender.getName() + " : /" + label
					+ " " + StringUtils.arrayToString(args, 0, " "));
	}

	protected abstract List<SubCommand> getSubCommands();
	protected abstract SubCommand getDefaultCommand();
}
