
package jdz.bukkitUtils.utils;

public class TimeUtils {

	public static String timeFromMinutes(int minutes) {
		return timeFromSeconds(minutes * 60);
	}

	public static String timeFromSeconds(int totalSeconds) {
		int days = totalSeconds / 86400;
		int hours = totalSeconds % 86400 / 3600;
		int minutes = totalSeconds % 86400 % 3600 / 60;
		int seconds = totalSeconds % 86400 % 3600 % 60;

		String rs = "";
		if (days > 0)
			rs = rs + days + "d ";
		if (hours > 0)
			rs = rs + hours + "h ";
		if (minutes > 0)
			rs = rs + minutes + "m ";
		if (seconds > 0)
			rs = rs + seconds + "s ";

		if (rs.equals(""))
			rs = "0s";
		else
			rs = rs.substring(0, rs.length() - 1);

		return rs;
	}
}
