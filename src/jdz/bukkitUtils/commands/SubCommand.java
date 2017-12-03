
package jdz.bukkitUtils.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandLabels;
import jdz.bukkitUtils.commands.annotations.CommandLongDescription;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandPermissions;
import jdz.bukkitUtils.commands.annotations.CommandPlayerOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;

public abstract class SubCommand {
	private final List<String> labels;

	public SubCommand() {
		CommandLabels commandLabels = this.getClass().getAnnotation(CommandLabels.class);
		CommandLabel label = this.getClass().getAnnotation(CommandLabel.class);
		if (commandLabels == null && label == null)
			throw new RuntimeException(
					getClass().getName() + " command doesn't have the required @CommandLabel annotation!");

		labels = new ArrayList<String>(commandLabels == null ? 1 : commandLabels.value().length);

		if (commandLabels != null)
			for (CommandLabel l : commandLabels.value())
				labels.add(l.value());
		else
			labels.add(label.value());
	}

	public boolean labelMatches(String label) {
		for (String s : labels)
			if (s.equalsIgnoreCase(label))
				return true;
		return false;
	}

	public boolean hasRequiredPermissions(CommandSender sender) {
		CommandPermissions perms = this.getClass().getAnnotation(CommandPermissions.class);
		if (perms == null)
			return true;

		for (CommandPermission perm : perms.value())
			if (!sender.hasPermission(perm.value()))
				return false;

		return true;
	}

	public int requiredArgs() {
		CommandRequiredArgs an = getClass().getAnnotation(CommandRequiredArgs.class);
		return an == null ? 0 : an.value();
	}

	public String getShortDescription() {
		CommandShortDescription desc = getClass().getAnnotation(CommandShortDescription.class);
		return desc == null ? "" : desc.value();
	}

	public String getUsage() {
		CommandUsage usage = getClass().getAnnotation(CommandUsage.class);
		return usage == null ? "" : usage.value();
	}

	public String getLongDescription() {
		CommandLongDescription desc = getClass().getAnnotation(CommandLongDescription.class);
		return desc == null ? getShortDescription() : desc.value();
	}

	public String getLabel() {
		return labels.get(0);
	}

	public List<String> getLabels() {
		return labels;
	}

	public boolean isPlayerOnly() {
		return (this.getClass().getAnnotation(CommandPlayerOnly.class) != null);
	}

	public abstract void execute(CommandSender sender, Set<String> flags, String... args);
	
	public boolean tryParse(String methodName, boolean showErrors, CommandSender sender, Set<String> flags, String... args) {
		for (Method method: this.getClass().getMethods())
			if (method.getName().equalsIgnoreCase(methodName))
				return tryParse(method, showErrors, sender, flags, args);
		
		throw new IllegalArgumentException("No method found with the name "+methodName);
	}

	/**
	 * Attempts to parse the given arguments into a given method's format
	 *  either m(CommandSender, Set<String> flags, int arg1, Player arg2...)
	 *  or     m(CommandSender, int arg1, Player arg2...)
	 * and invokes said method, or sends the sender error messages if something went wrong.
	 * 
	 * @param method
	 * @param showErrors
	 * @param sender
	 * @param flags
	 * @param args
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected boolean tryParse(Method method, boolean showErrors, CommandSender sender, Set<String> flags, String... args) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		if (parameterTypes.length < 1 || !parameterTypes[0].equals(CommandSender.class))
			throw new IllegalArgumentException("Method "+method.getName()+"Must have a CommandSender argument");
		
		int startIndex = 1;
		if (parameterTypes.length > 1 && parameterTypes[1].equals(Set.class))
			startIndex = 2;
		
		try {			
			Object[] newArgs = new Object[parameterTypes.length];
			newArgs[0] = sender;
			if (startIndex == 2)
				newArgs[1] = flags;
			
			for (int i=startIndex; i<parameterTypes.length; i++) {
				
				if (parameterTypes[i].equals(String.class))
					newArgs[i] = args[i-startIndex];
				
				else if (parameterTypes[i].equals(Integer.class) || parameterTypes[i].equals(int.class)) {
					try { newArgs[i] = Integer.parseInt(args[i-startIndex]); }
					catch(NumberFormatException e) {
						if (showErrors) {
							sender.sendMessage(ChatColor.RED+"Argument #"+(i-startIndex+1)+" must be an integer");
							if (!getUsage().equals(""))
								sender.sendMessage(ChatColor.RED+"Usage: "+getUsage());
						}
						return false;
					}
				}
				
				else if (parameterTypes[i].equals(Long.class) || parameterTypes[i].equals(long.class)) {
					try { newArgs[i] = Long.parseLong(args[i-startIndex]); }
					catch(NumberFormatException e) {
						if (showErrors) {
							sender.sendMessage(ChatColor.RED+"Argument #"+(i-startIndex+1)+" must be an integer");
							if (!getUsage().equals(""))
								sender.sendMessage(ChatColor.RED+"Usage: "+getUsage());
						}
						return false;
					}
				}
				
				else if (parameterTypes[i].equals(Double.class) || parameterTypes[i].equals(double.class)) {
					try { newArgs[i] = Double.parseDouble(args[i-startIndex]); }
					catch(NumberFormatException e) {
						if (showErrors) {
							sender.sendMessage(ChatColor.RED+"Argument #"+(i-startIndex+1)+" must be a number");
							if (!getUsage().equals(""))
								sender.sendMessage(ChatColor.RED+"Usage: "+getUsage());
						}
						return false;
					}
				}
				
				else if (parameterTypes[i].equals(Player.class)) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(args[i-startIndex]);
					if (!player.hasPlayedBefore()) {
						sender.sendMessage(ChatColor.RED+"'"+args[i-startIndex]+"' has never logged in before!");
						return false;
					}
					if (!player.isOnline()) {
						sender.sendMessage(ChatColor.RED+"'"+args[i-startIndex]+"' is not online!");
						return false;
					}
					newArgs[i] = (Player)player;
				}
				
				else if (parameterTypes[i].equals(OfflinePlayer.class)){
					OfflinePlayer player = Bukkit.getOfflinePlayer(args[i-startIndex]);
					if (!player.hasPlayedBefore()) {
						sender.sendMessage(ChatColor.RED+"'"+args[i-startIndex]+"' has never logged in before!");
						return false;
					}
					newArgs[i] = player;
				}
				
			}

			method.invoke(this, newArgs);
			
			return true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
	}
}
