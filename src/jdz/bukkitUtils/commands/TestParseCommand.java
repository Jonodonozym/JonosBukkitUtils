
package jdz.bukkitUtils.commands;

import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;

class TestParseCommand {
	@CommandLabel("testCommand")
	private static class testClass extends SubCommand {

		@CommandMethod(withSender = true, parseFlags = false)
		public void onWarp(CommandSender sender, double testInt, String testString) {
			assert sender == null;
			assert testInt == 2.5;
			assert testString.equals("dsag");
			System.out.println("Test passed successfully");
		}
	}
}
