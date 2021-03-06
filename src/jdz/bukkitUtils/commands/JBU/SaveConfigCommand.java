package jdz.bukkitUtils.commands.JBU;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandOpOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.bukkitUtils.configuration.ConfigSaveEvent;

@CommandLabel("saveConfig")
@CommandLabel("sc")
@CommandRequiredArgs(1)
@CommandUsage("sc [Plugin]")
@CommandOpOnly
public class SaveConfigCommand extends Command {

	@Override
	public void execute(CommandSender sender, String... args) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(args[0]);
		if (plugin == null)
			sender.sendMessage(ChatColor.RED + "No plugin found called ");
	}

	public void reload(CommandSender sender, Plugin plugin) {
		new ConfigSaveEvent(plugin).call();
		sender.sendMessage(ChatColor.GREEN + "Config reloaded for " + plugin.getName());
	}

}
