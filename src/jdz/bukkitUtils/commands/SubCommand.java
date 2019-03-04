
package jdz.bukkitUtils.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandLabels;
import jdz.bukkitUtils.commands.annotations.CommandLongDescription;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandOpOnly;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandPermissions;
import jdz.bukkitUtils.commands.annotations.CommandPlayerOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.bukkitUtils.components.Pair;
import lombok.Getter;

public abstract class SubCommand {
	@Getter private final int requiredArgs = getClass().getAnnotation(CommandRequiredArgs.class) == null ? 0
			: getClass().getAnnotation(CommandRequiredArgs.class).value();
	@Getter private final List<String> labels = loadLabels();
	@Getter private final List<String> permissions = loadPermissions();
	@Getter private final boolean OPOnly = getClass().getAnnotation(CommandOpOnly.class) != null;
	@Getter private final boolean playerOnly = getClass().getAnnotation(CommandPlayerOnly.class) != null;
	@Getter private final String usage = getClass().getAnnotation(CommandUsage.class) == null ? ""
			: getClass().getAnnotation(CommandUsage.class).value();
	@Getter private final String shortDescription = getClass().getAnnotation(CommandShortDescription.class) == null ? ""
			: getClass().getAnnotation(CommandShortDescription.class).value();
	@Getter private final String longDescription = getClass().getAnnotation(CommandLongDescription.class) == null ? ""
			: getClass().getAnnotation(CommandLongDescription.class).value();
	private final List<Method> methods = loadMethods();

	private List<String> loadLabels() {
		CommandLabels commandLabels = getClass().getAnnotation(CommandLabels.class);
		CommandLabel label = getClass().getAnnotation(CommandLabel.class);

		if (commandLabels == null && label == null)
			throw new RuntimeException(
					getClass().getName() + " command doesn't have the required @CommandLabel annotation!");

		List<String> labels = new ArrayList<>();

		if (commandLabels != null)
			for (CommandLabel l : commandLabels.value())
				labels.add(l.value());
		else
			labels.add(label.value());
		return labels;
	}

	private List<String> loadPermissions() {
		List<String> permissions = new ArrayList<>();
		CommandPermissions perms = getClass().getAnnotation(CommandPermissions.class);
		if (perms != null)
			for (CommandPermission p : perms.value())
				permissions.add(p.value());

		CommandPermission perm = getClass().getAnnotation(CommandPermission.class);
		if (perm != null)
			permissions.add(perm.value());
		return permissions;
	}

	private List<Method> loadMethods() {
		List<Method> methods = new ArrayList<>();
		for (Method method : getClass().getDeclaredMethods())
			if (method.getAnnotation(CommandMethod.class) != null && method.getModifiers() == Modifier.PUBLIC)
				methods.add(method);
		return methods;
	}

	public boolean labelMatches(String label) {
		for (String s : labels)
			if (s.equalsIgnoreCase(label))
				return true;
		return false;
	}

	public boolean hasRequiredPermissions(CommandSender sender) {
		if (sender.isOp() || sender instanceof ConsoleCommandSender)
			return true;
		for (String perm : permissions)
			if (!sender.hasPermission(perm))
				return false;
		return true;
	}

	public String getLabel() {
		return labels.get(0);
	}

	public void execute(CommandSender sender, String... args) {
		List<Method> methods = getBestMethods(args);
		if (methods.size() == 1)
			tryParse(methods.get(0), true, sender, args);
		else
			for (Method method : methods)
				if (tryParse(method, false, sender, args))
					return;
	}

	@Deprecated
	public boolean tryParse(String methodName, boolean showErrors, CommandSender sender, String... args) {
		for (Method method : getClass().getMethods())
			if (method.getName().equalsIgnoreCase(methodName))
				return tryParse(method, showErrors, sender, args);

		throw new IllegalArgumentException("No method found with the name " + methodName);
	}

	private List<Method> getBestMethods(String... args) {
		List<Method> methods = new ArrayList<>();
		for (Method method : this.methods) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			CommandMethod annotation = method.getAnnotation(CommandMethod.class);
			if (annotation == null)
				continue;
			if (annotation.withSender() && !parameterTypes[0].equals(CommandSender.class)
					&& !parameterTypes[0].equals(Player.class))
				continue;
			if (annotation.parseFlags() && !parameterTypes[annotation.withSender() ? 1 : 0].equals(Set.class))
				continue;
			if (args.length == parameterTypes.length - (annotation.withSender() ? 1 : 0)
					- (annotation.parseFlags() ? 1 : 0))
				methods.add(method);
		}
		return methods;
	}

	protected boolean tryParse(Method method, boolean showErrors, CommandSender sender, String... args) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		CommandMethod annotation = method.getAnnotation(CommandMethod.class);

		Object[] newArgs = new Object[parameterTypes.length];
		int startParsingIndex = 0;

		if (annotation.withSender()) {
			if (parameterTypes.length == 0)
				throw new IllegalStateException("Method " + method.getName() + " must have a CommandSender argument");
			if (parameterTypes[0].equals(Player.class)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You must be a player to do that");
					return false;
				}
				newArgs[0] = sender;
			}
			else {
				if (!parameterTypes[0].equals(CommandSender.class))
					throw new IllegalArgumentException(
							"Method " + method.getName() + " Must have a CommandSender argument");
				newArgs[0] = sender;
			}
			startParsingIndex++;
		}

		if (annotation.parseFlags())
			if (parameterTypes.length > 1 && parameterTypes[1].equals(Set.class)) {
				Pair<String[], Set<String>> pair = parseFlags(args);
				args = pair.getKey();
				newArgs[startParsingIndex] = pair.getValue();
				startParsingIndex++;
			}
			else
				throw new IllegalArgumentException(
						"Method " + method.getName() + " Must have a Set<String> argument for parsing flags");

		for (int i = startParsingIndex; i < parameterTypes.length; i++)
			try {
				newArgs[i] = CommandArgumentParsers.getParser(parameterTypes[i]).parse(args[i - startParsingIndex]);
			}
			catch (IllegalArgumentException e) {
				if (showErrors)
					sender.sendMessage(ChatColor.RED + e.getMessage());
				return false;
			}

		try {
			method.invoke(this, newArgs);
		}
		catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private Pair<String[], Set<String>> parseFlags(String... args) {
		Set<String> flags = new HashSet<>();
		List<String> newArgs = new ArrayList<>();

		for (String arg : args)
			if (arg.startsWith("-"))
				flags.add(arg.substring(1));
			else
				newArgs.add(arg);

		return new Pair<>(newArgs.toArray(new String[newArgs.size()]), flags);
	}
}
