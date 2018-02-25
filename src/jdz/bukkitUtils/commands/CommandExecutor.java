package jdz.bukkitUtils.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.commands.annotations.CommandExecutorAlias;
import jdz.bukkitUtils.commands.annotations.CommandExecutorAliases;
import jdz.bukkitUtils.commands.annotations.CommandExecutorOpOnly;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPermission;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPermissions;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPlayerOnly;
import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;

public abstract class CommandExecutor implements org.bukkit.command.CommandExecutor {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final Map<JavaPlugin, FileLogger> loggers = new HashMap<JavaPlugin, FileLogger>();

	protected final JavaPlugin plugin;
	protected final boolean logCommands;
	protected final String label;
	protected final List<String> aliases;
	protected final List<String> permissions;
	protected final FileLogger fileLogger;

	protected final HelpCommand helpCommand;
	protected SubCommand defaultCommand = null;
	protected final Map<SubCommand, JavaPlugin> extraCommands = new HashMap<SubCommand, JavaPlugin>();

	protected boolean isHelpEnabled = true;
	protected boolean isRegistered = false;

	CommandExecutor() {
		this(null, "", false);
	}

	public CommandExecutor(JavaPlugin plugin, String label, boolean logCommands) {
		this.logCommands = logCommands;
		this.label = label;

		CommandExecutorAliases commandAliases = this.getClass().getAnnotation(CommandExecutorAliases.class);
		CommandExecutorAlias alias = this.getClass().getAnnotation(CommandExecutorAlias.class);

		List<String> aliases = new ArrayList<String>(commandAliases == null ? 1 : commandAliases.value().length);

		if (commandAliases != null)
			for (CommandExecutorAlias l : commandAliases.value())
				aliases.add(l.value());
		else if (alias != null)
			aliases.add(alias.value());
		aliases.add(label);

		this.aliases = Collections.unmodifiableList(aliases);

		CommandExecutorPermissions commandPerms = this.getClass().getAnnotation(CommandExecutorPermissions.class);
		CommandExecutorPermission perm = this.getClass().getAnnotation(CommandExecutorPermission.class);

		List<String> perms = new ArrayList<String>(commandPerms == null ? 1 : commandPerms.value().length);

		if (commandPerms != null)
			for (CommandExecutorPermission l : commandPerms.value())
				perms.add(l.value());
		else if (perm != null)
			perms.add(perm.value());

		this.permissions = Collections.unmodifiableList(perms);

		this.plugin = plugin;

		helpCommand = new HelpCommand(this);
		defaultCommand = helpCommand;

		if (plugin != null) {
			if (!loggers.containsKey(plugin))
				loggers.put(plugin, new FileLogger(plugin, "CommandLogs"));
			fileLogger = loggers.get(plugin);
		}
		else
			fileLogger = null;
	}

	public void register() {
		if (!isRegistered)
			for (String label : aliases) {
				if (plugin.getCommand(label) == null)
					new FileLogger(plugin).createErrorLog(new IllegalArgumentException(),
							"No command found in " + plugin.getName() + "'s plugin.yml file labeled '" + label + "'");
				plugin.getCommand(label).setExecutor(this);
				isRegistered = true;
			}
	}

	public boolean isRegistered() {
		return isRegistered;
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
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		CommandExecutorPlayerOnly cepo = this.getClass().getAnnotation(CommandExecutorPlayerOnly.class);
		if (cepo != null && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
			return true;
		}

		CommandExecutorOpOnly ceopo = this.getClass().getAnnotation(CommandExecutorOpOnly.class);
		if (ceopo != null && !sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You don't have enough permissions to do that!");
			return true;
		}

		for (String s : permissions) {
			if (!sender.hasPermission(s)) {
				sender.sendMessage(ChatColor.RED + "You are missing the permission node " + s);
				return true;
			}
		}

		if (args.length == 0) {
			execute(getDefaultCommand(), sender, new HashSet<String>(), args);
			return true;
		}

		Set<String> flags = new HashSet<String>();
		for (String arg : args)
			if (arg.startsWith("-"))
				flags.add(arg.substring(1));

		String[] newArgs = new String[args.length - flags.size()];
		int i = 0;
		for (String arg : args)
			if (!arg.startsWith("-"))
				newArgs[i++] = arg;

		List<SubCommand> commands = new ArrayList<SubCommand>(getSubCommands());

		if (isHelpEnabled)
			commands.add(helpCommand);

		for (SubCommand command : commands) {
			if (command.labelMatches(newArgs[0])) {
				String[] subArgs = new String[newArgs.length - 1];
				for (int j = 0; j < subArgs.length; j++)
					subArgs[j] = newArgs[j + 1];
				execute(command, sender, flags, subArgs);
				return true;
			}
		}

		// extra commands
		List<SubCommand> extraCommands = new ArrayList<SubCommand>(this.extraCommands.keySet());
		for (SubCommand command : extraCommands) {

			if (Bukkit.getPluginManager().getPlugin(this.extraCommands.get(command).getName()) == null) {
				this.extraCommands.remove(command);
				continue;
			}

			if (command.labelMatches(newArgs[0])) {
				String[] subArgs = new String[newArgs.length - 1];
				for (int j = 0; j < subArgs.length; j++)
					subArgs[j] = newArgs[j + 1];
				execute(command, sender, flags, subArgs);
				return true;
			}
		}

		if (!isHelpEnabled && getDefaultCommand() instanceof HelpCommand)
			return false;
		else
			execute(getDefaultCommand(), sender, flags, args);

		return true;
	}

	public void execute(SubCommand command, CommandSender sender, Set<String> flags, String... args) {
		if (command.requiredArgs() > args.length) {
			sender.sendMessage(ChatColor.RED + "Insufficient arguments");
			if (!command.getUsage().equals(""))
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + command.getUsage());
		}
		else if (command.isPlayerOnly() && !(sender instanceof Player))
			sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
		else if (command.isOPOnly() && !sender.isOp())
			sender.sendMessage(ChatColor.RED + "You don't have enough permissions to do that!");
		else
			executeIfHasPerms(command, sender, flags, args);
	}

	private final void executeIfHasPerms(SubCommand command, CommandSender sender, Set<String> flags, String... args) {
		logCommand(sender, command.getLabel(), args);
		if (command.hasRequiredPermissions(sender)) {
			if (command.isAsync())
				Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
					command.execute(sender, flags, args);
				});
			else
				command.execute(sender, flags, args);
		}
		else
			sender.sendMessage(ChatColor.RED + "You don't have the permissions to do that");
	}

	private final void logCommand(CommandSender sender, String label, String[] args) {
		if (logCommands)
			fileLogger.log("[" + dtf.format(LocalDateTime.now()) + "] " + sender.getName() + " : /" + this.label + " "
					+ label + " " + StringUtils.arrayToString(args, 0, " "));
	}

	public void setDefaultCommand(SubCommand c) {
		defaultCommand = c;
		helpCommand.reload();
	}

	public SubCommand getDefaultCommand() {
		return defaultCommand;
	}

	public final HelpCommand getHelpCommand() {
		return helpCommand;
	}

	protected abstract List<SubCommand> getSubCommands();

	public void addCommand(SubCommand c, JavaPlugin plugin) {
		extraCommands.put(c, plugin);
		helpCommand.reload();
	}
}
