package org.frustra.feather.server.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.CommandException;
import org.frustra.feather.server.CommandUsageException;
import org.frustra.feather.server.Entity;
import org.frustra.feather.server.Player;

public class InfoCommand extends Command {
	public String getName() {
		return "info";
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public final static long ONE_MINUTE = 60;
	public final static long ONE_HOUR = ONE_MINUTE * 60;
	public final static long ONE_DAY = ONE_HOUR * 24;

	public static String secondsToString(long duration) {
		String res = "";
		long tmp = 0;
		if (duration >= 1) {
			tmp = duration / ONE_DAY;
			if (tmp > 0) {
				duration -= tmp * ONE_DAY;
				res += tmp + " day";
				if (tmp > 1) res += "s";
				if (duration >= ONE_MINUTE) res += ", ";
			}

			tmp = duration / ONE_HOUR;
			if (tmp > 0) {
				duration -= tmp * ONE_HOUR;
				res += tmp + " hour";
				if (tmp > 1) res += "s";
				if (duration >= ONE_MINUTE) res += ", ";
			}

			tmp = duration / ONE_MINUTE;
			if (tmp > 0) {
				duration -= tmp * ONE_MINUTE;
				res += tmp + " minute";
				if (tmp > 1) res += "s";
			}
			return res;
		} else {
			return "0 seconds";
		}
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 1) {
			Player target = Bootstrap.server.fetchPlayer(arguments[0]);
			if (target == null) {
				throw new CommandException(target + " hasn't played here");
			}

			source.sendMessage("==== %s ====", target);
			if (target.instance != null) {
				source.sendMessage("Currently online");
			} else {
				source.sendMessage("Last Seen: %s ago", secondsToString(System.currentTimeMillis() / 1000 - target.lastSeen));
			}
			source.sendMessage("Karma: %s", new DecimalFormat("#.##").format(target.karma));
			source.sendMessage("Time online: %s", secondsToString(target.playTime));
		} else {
			throw new CommandUsageException(this);
		}
	}

	public String getUsage(Entity source) {
		return "/info <player>";
	}

	public List<String> getCompletionList(Entity source, String[] arguments) {
		ArrayList<String> list = new ArrayList<String>();
		if (arguments.length == 1) {
			for (Player p : Bootstrap.server.getPlayers()) {
				if (p.getName().startsWith(arguments[0])) {
					list.add(p.getName());
				}
			}
		}
		return list;
	}
}
