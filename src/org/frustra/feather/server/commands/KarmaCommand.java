package org.frustra.feather.server.commands;

import java.util.ArrayList;
import java.util.List;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.CommandException;
import org.frustra.feather.server.CommandUsageException;
import org.frustra.feather.server.Entity;
import org.frustra.feather.server.Player;

public class KarmaCommand extends Command {
	public String getName() {
		return "karma";
	}

	public boolean hasPermission(Entity source) {
		return source.isOperator();
	}

	public void execute(Entity source, String[] arguments) {
		String targetName = source.getName(), amountString = "0";
		if (arguments.length == 2) {
			amountString = arguments[1];
		} else if (arguments.length == 3) {
			targetName = arguments[1];
			amountString = arguments[2];
		} else {
			throw new CommandUsageException(this);
		}

		Player target = Bootstrap.server.getPlayer(targetName);
		if (target != null) {
			try {
				int amount = Integer.parseInt(amountString);
				if (arguments[0].equals("set")) {
					target.setKarma(amount);
				} else if (arguments[0].equals("add")) {
					target.setKarma(target.getKarma() + amount);
				} else {
					throw new CommandUsageException(this);
				}
			} catch (NumberFormatException e) {
				throw new CommandException("Invalid amount specified: " + amountString);
			}
			source.sendMessage("%s's karma set to %s", target, target.karma);
		} else {
			throw new CommandException("Target player not found");
		}
	}

	public String getUsage(Entity source) {
		return "/karma <set|add> [target player] <amount>";
	}

	public List<String> getCompletionList(Entity source, String[] arguments) {
		ArrayList<String> list = new ArrayList<String>();
		if (arguments.length == 1) {
			list.add("set");
			list.add("add");
		} else if (arguments.length == 2) {
			for (Player p : Bootstrap.server.getPlayers()) {
				if (p.getName().startsWith(arguments[1])) {
					list.add(p.getName());
				}
			}
		}
		return list;
	}
}
