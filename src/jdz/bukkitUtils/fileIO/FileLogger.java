/**
 * FileLogger.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.fileIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.JonosBukkitUtils;
import jdz.bukkitUtils.events.Listener;
import lombok.Setter;

/**
 * Lets you log plugin messages in a file
 * Also lets you log errors in the file instead of displaying a big ugly message
 * on the console
 *
 * @author Jonodonozym
 */
public class FileLogger implements Listener {
	private static final Executor IOExecutor = Executors.newCachedThreadPool();

	private BufferedWriter bufferedWriter = null;
	private final Plugin plugin;
	private final String logName;
	private final String logDirectory;
	@Setter private boolean printToConsole = false;
	@Setter private boolean writeToLog = true;
	@Setter private boolean newLog = false;

	public FileLogger(Plugin plugin) {
		this(plugin, "Log", false);
	}

	public FileLogger(Plugin plugin, String logName) {
		this(plugin, logName, false);
	}

	public FileLogger(Plugin plugin, String logName, boolean newLogFileEachRun) {
		this.plugin = plugin;
		this.logName = logName;
		this.logDirectory = plugin.getDataFolder() + File.separator + "Logs";
		this.newLog = newLogFileEachRun;
		registerEvents(plugin);
	}

	/**
	 * Starts a new log file
	 * 
	 * you probably never need to do this, I just use it for a few methods myself
	 * and thought I should share.
	 * Aren't I a wonderful developer?
	 */
	private void startNewLog() {
		try {
			if (bufferedWriter != null)
				bufferedWriter.close();

			File file = new File(logDirectory, logName + File.separator + getTimestamp() + ".txt");
			if (!newLog)
				file = new File(logDirectory, logName + ".txt");
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists())
				file.createNewFile();

			bufferedWriter = new BufferedWriter(new FileWriter(file, true));
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Logs a message to the current log file
	 * creates a new log file if one isn't already in use
	 * 
	 * @param message
	 */
	public void log(String message) {
		try {
			if (writeToLog) {
				if (bufferedWriter == null)
					startNewLog();
				String timestamp = newLog ? getTimestampShort() : "[" + getTimestamp() + "]";
				bufferedWriter.append(timestamp + "  " + message + System.lineSeparator());
				IOExecutor.execute(() -> {
					try {
						bufferedWriter.flush();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
			if (printToConsole)
				plugin.getLogger().info(message);
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@EventHandler
	public void onUnload(PluginDisableEvent event) {
		if (event.getPlugin().equals(plugin)) {
			try {
				if (bufferedWriter != null)
					bufferedWriter.flush();
			}
			catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Writes an exception's stack trace to an error log file, given an exception
	 * and extra information you might want to tack on to help debugging
	 * 
	 * @param exception
	 * @param extraData
	 */
	public void createErrorLog(Exception exception, String... extraData) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		if (extraData.length > 0) {
			pw.println();
			pw.println("Extra data:");
			for (String s : extraData)
				pw.println('\t' + s);
		}
		pw.flush();
		String exceptionAsString = sw.toString();
		createErrorLog(logDirectory + File.separator + "Errors" + File.separator + exception.getClass().getSimpleName()
				+ getTimestamp() + ".txt", exceptionAsString);
	}

	/**
	 * Writes an error message to an error log file
	 * 
	 * @param error
	 */
	public void createErrorLog(String error) {
		createErrorLog(logDirectory + File.separator + "Errors" + File.separator + "Error " + getTimestamp() + ".txt",
				error);
	}

	public void createErrorLog(String fileDir, String error) {
		plugin.getLogger().severe("An error occurred. Check the Error log file for details.");

		int i = 0;
		for (String s : error.split("\n")) {
			plugin.getLogger().severe(s);
			if (++i > 3) {
				plugin.getLogger().severe("...");
				break;
			}
		}

		File file = new File(fileDir);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		String header = "An error occurred in the plugin. If you can't work out the issue from this file, send this file to the plugin developer with a description of the failure\n";
		header += "Plugin name: " + plugin.getName() + "\n";
		header += "Plugin version: " + plugin.getDescription().getVersion() + "\n";
		header += "JBU version: " + JonosBukkitUtils.getInstance().getDescription().getVersion() + "\n";

		writeFile(header, error, file);
	}

	public String getTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd  HH-mm-ss-SSS").format(new Date());
	}

	public String getTimestampShort() {
		return "[" + new SimpleDateFormat("HH-mm-ss.SSS").format(new Date()) + "]";
	}

	private void writeFile(String header, String contents, File file) {
		try {
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
			if (header != "") {
				String[] lines = header.split("\n");
				for (String line : lines) {
					bfw.write(line);
					bfw.newLine();
				}
				bfw.newLine();
			}
			String[] lines = contents.split("\n");
			for (String line : lines) {
				bfw.newLine();
				bfw.write(line);
			}
			bfw.close();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
