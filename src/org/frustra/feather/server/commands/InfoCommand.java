package org.frustra.feather.server.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
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

	private static String formatDate(long unix) {
		Calendar now = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();

		SimpleDateFormat format = new SimpleDateFormat("M d HH:mm");
		Date date = new Date(unix * 1000);
		cal.setTime(date);
		if (cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
			format = new SimpleDateFormat("M d, y HH:mm");
		}
		return format.format(date);
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 1) {
			Player target = Bootstrap.server.fetchPlayer(arguments[0]);

			if (target == null) {
				source.sendMessage("%s hasn't ever played here", new Object[] { target });
			} else {
				String lastSeenString = "is currently playing";
				if (target.instance == null) {
					lastSeenString = "was last seen " + formatDate(target.lastSeen);
				}
				source.sendMessage("%s has %s karma, first joined %s, and %s", new Object[] { target, target.karma, formatDate(target.firstJoin), lastSeenString });
			}
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
