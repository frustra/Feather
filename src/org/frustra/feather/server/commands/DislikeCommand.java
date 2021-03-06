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

public class DislikeCommand extends Command {
	public String getName() {
		return "dislike";
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length != 1) throw new CommandUsageException(this);

		long currentTime = System.currentTimeMillis() / 1000;
		Player p = source.getPlayer();
		Player target = Bootstrap.server.getPlayer(arguments[0]);
		if (target != null && !p.equals(target)) {
			if (p.getKarma() > 1) {
				if (currentTime - p.lastLike > 3600) {
					p.lastLike = currentTime;
					double amount = Math.log(p.getKarma()) / Math.log(4);
					target.setKarma(target.getKarma() - amount / 2.0);
					String formatedAmount = new DecimalFormat("#.##").format(amount / 2.0);
					source.sendMessage("%s has lost %s karma", target, formatedAmount);
					target.sendMessage("You have lost %s karma", formatedAmount);
				} else {
					throw new CommandException("You can't dislike for another " + InfoCommand.secondsToString(3600 - (currentTime - p.lastLike)));
				}
			} else {
				throw new CommandException("You don't have enough karma for that!");
			}
		} else if (target != null) {
			throw new CommandException("You can't dislike yourself!");
		} else {
			throw new CommandException("Target player not found");
		}
	}

	public String getUsage(Entity source) {
		return "/dislike <player>";
	}

	public List<String> getCompletionList(Entity source, String[] arguments) {
		ArrayList<String> list = new ArrayList<String>();
		if (arguments.length == 1) {
			for (Player p : Bootstrap.server.getPlayers()) {
				if (p.getName().startsWith(arguments[1])) {
					list.add(p.getName());
				}
			}
		}
		return list;
	}
}
