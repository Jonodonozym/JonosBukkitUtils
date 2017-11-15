
package jdz.bukkitUtils.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.commands.annotations.CommandExecutorPlayerOnly;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;

public abstract class CommandExecutor implements org.bukkit.command.CommandExecutor {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private final JavaPlugin plugin;
	private final boolean logCommands;
	private final String label;
	private final FileLogger fileLogger;
	
	private final HelpCommand helpCommand;
	private SubCommand defaultCommand = null;
	
	private boolean isHelpEnabled = true;
	
	CommandExecutor() {
		this(null, "", false);
	}
	
	public CommandExecutor(JavaPlugin plugin, String label, boolean logCommands) {
		this.logCommands = logCommands;
		this.label = label;
		this.plugin = plugin;
		
		this.helpCommand = new HelpCommand(this);
		setDefaultCommand(helpCommand);
		
		if (plugin != null)
			fileLogger = new FileLogger(plugin, "CommandLogs");
		else
			fileLogger = null;
	}
	
	public void register() {
		if (!isRegistered()) {
			plugin.getCommand(label).setExecutor(this);
			isregistered = true;
		}
	}
	
	private boolean isregistered = false;
	public boolean isRegistered() {
		return isregistered;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void disableHelpCommand() {
		isHelpEnabled = false;
	}
	
	public void enableHelpCommand() {
		isHelpEnabled = true;
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {		
		CommandExecutorPlayerOnly cepo = this.getClass().getAnnotation(CommandExecutorPlayerOnly.class);
		if (cepo != null && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED+"You must be a player to do that!");
			return true;
		}
		
		if (args.length == 0) {
			execute(getDefaultCommand(), sender, args);
			return true;
		}
		
		List<SubCommand> commands = getSubCommands();
		if (isHelpEnabled) commands.add(helpCommand);
		for (SubCommand command : commands) {
			if (command.labelMatches(args[0])) {
				String[] subArgs = new String[args.length - 1];
				for (int i = 0; i < subArgs.length; i++)
					subArgs[i] = args[i + 1];
				execute(command, sender, subArgs);
				return true;
			}
		}

		if (!isHelpEnabled && getDefaultCommand() instanceof HelpCommand)
			return false;
		else
			execute(getDefaultCommand(), sender, args);
		
		return true;
	}
	
	public final void execute(SubCommand command, CommandSender sender, String...args) {
		if (command.requiredArgs() > args.length) {
			sender.sendMessage(ChatColor.RED+"Insufficient arguments");
			if (!command.getUsage().equals(""))
				sender.sendMessage(ChatColor.RED+"Usage: "+command.getUsage());
		}
		else if (command.isPlayerOnly() && !(sender instanceof Player))
			sender.sendMessage(ChatColor.RED+"You must be a player to do that!");
		else
			executeIfHasPerms(command, sender, args);
	}
	
	private final void executeIfHasPerms(SubCommand command, CommandSender sender, String...args) {
		logCommand(sender, command.getLabel(), args);		
		if (command.hasRequiredPermissions(sender))
			command.execute(sender, args);
		else
			sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that");
	}	
	
	private final void logCommand(CommandSender sender, String label, String[] args) {
		if (logCommands)
			fileLogger.log("[" + dtf.format(LocalDateTime.now()) + "] " + sender.getName() + " : /" + label
					+ " " + StringUtils.arrayToString(args, 0, " "));
	}

	public void setDefaultCommand(SubCommand c) { defaultCommand = c; }
	public SubCommand getDefaultCommand() { return defaultCommand; }
	
	public final HelpCommand getHelpCommand() { return helpCommand; }

	protected abstract List<SubCommand> getSubCommands();
}
