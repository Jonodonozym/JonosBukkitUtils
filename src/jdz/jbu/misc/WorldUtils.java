
package jdz.jbu.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import jdz.jbu.JonosBukkitUtils;
import jdz.jbu.fileIO.FileLogger;

public final class WorldUtils {	
	public static String locationToString(Location l) {
		return l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ()+","+l.getPitch()+","+l.getYaw();
	}
	
	public static Location locationFromString(String s){
		String[] args = s.split(",");
		try{
		World world = Bukkit.getWorld(args[0]);
		return new Location(world,
				Integer.parseInt(args[1])+0.5, Integer.parseInt(args[2]),
				Integer.parseInt(args[3])+0.5, Float.parseFloat(args[4]), Float.parseFloat(args[5]));
		}
		catch (Exception e){
			new FileLogger(JonosBukkitUtils.instance).createErrorLog(e, "Error parsing location with args: "+args);
			return null;
		}
	}
	
	public static Location getNearestLocationUnder(Location l){
		Location location = new Location(l.getWorld(), l.getBlockX()+0.5, l.getBlockY(), l.getBlockZ()+0.5);
		while (location.getBlock().isEmpty()){
			location = location.add(0, 1, 0);
			if (location.getY() < 0){
				return null;
			}
		}
		return location;
	}
}
