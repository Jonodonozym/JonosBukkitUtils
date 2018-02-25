
package jdz.bukkitUtils.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.annotations.CommandLabel;

class TestParseCommand {

	@CommandLabel("testCommand")
	private static class testClass extends SubCommand {

		@Override
		public void execute(CommandSender sender, Set<String> flags, String... args) {
			tryParse("onWarp", false, null, flags, args);
		}

		@SuppressWarnings("unused") // used via reflection :O
		public void onWarp(CommandSender sender, double testInt, String testString) {
			assert sender == null;
			assert testInt == 2.5;
			assert testString.equals("dsag");
			System.out.println("Test passed successfully");
		}
	}
}
