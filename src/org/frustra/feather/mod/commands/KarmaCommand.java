package org.frustra.feather.mod.commands;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.feather.mod.server.Entity;
import org.frustra.feather.mod.server.Player;

public class KarmaCommand extends Command {
	public String getName() {
		return "karma";
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public void execute(Entity source, String[] arguments) {
		String targetName = source.getName(), amountString = "0";
		if (arguments.length == 2) {
			amountString = arguments[1];
		} else if (arguments.length == 3) {
			targetName = arguments[1];
			amountString = arguments[2];
		} else {
			source.sendMessage("Invalid usage: wrong number of arguments");
			return; // TODO throw a ch
		}

		Player target = Bootstrap.server.getPlayer(targetName);
		if (target != null) {
			int amount = Integer.parseInt(amountString);
			if (arguments[0].equals("set")) {
				target.setKarma(amount);
			} else if (arguments[0].equals("add")) {
				target.setKarma(target.getKarma() + amount);
			} else {
				source.sendMessage("Invalid usage: missing action");
				return; // TODO throw a ch
			}
			source.sendMessage("%s's karma set to %s", new Object[] { target, target.karma });
		} else {
			source.sendMessage("Target player not found");
		}
	}

	public String getUsage(Entity source) {
		return "/karma <set|add> <amount>";
	}
}
