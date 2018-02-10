/**
 * TimedTask.java
 *
 * Created by Jonodonozym on god knows when
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Oct 5, 2017 9:22:58 PM
 */

package jdz.bukkitUtils.misc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Asynchronosly runs a task on a loop for you
 * e.g. new TimedTask(20, ()-> {do stuff here}).start();
 * 
 * can .start() and .stop() at will, or just .run() to run it once.
 * 
 * Though if all you're doing is .run() with this class, 
 * you're a special kind of retard.
 *
 * @author Jonodonozym
 */
public final class TimedTask {
	private boolean isRunning = false;
	private final Runnable runnable;
	private final int tickInterval;
	private final JavaPlugin plugin;
	
	private int taskID = -1;
	
	public TimedTask(JavaPlugin plugin, int tickInterval, Runnable r){
		this.tickInterval = tickInterval;
		this.plugin = plugin;
		runnable = r;
	}
	
	/**
	 * Runs the task a single time
	 */
	public void run(){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
	}
	
	/**
	 * Starts the task on a loop if it is not already running
	 */
	public void start() {
		if (!isRunning) {
			taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, tickInterval, tickInterval).getTaskId();
			isRunning = true;
		}
	}

	/**
	 * Stops the task if it is currently looping
	 */
	public void stop() {
		if (isRunning) {
			Bukkit.getScheduler().cancelTask(taskID);
			isRunning = false;
		}
	}

	public boolean isRunning(){
		return isRunning;
	}
}
