package org.frustra.feather.mod.commands;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.feather.mod.Command;
import org.frustra.feather.mod.CommandException;
import org.frustra.feather.mod.CommandUsageException;
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
			throw new CommandUsageException(this);
		}

		Player target = Bootstrap.server.getPlayer(targetName);
		if (target != null) {
			int amount = Integer.parseInt(amountString);
			if (arguments[0].equals("set")) {
				target.setKarma(amount);
			} else if (arguments[0].equals("add")) {
				target.setKarma(target.getKarma() + amount);
			} else {
				throw new CommandUsageException(this);
			}
			source.sendMessage("%s's karma set to %s", new Object[] { target, target.karma });
		} else {
			throw new CommandException("Target player not found");
		}
	}

	public String getUsage(Entity source) {
		return "/karma <set|add> <amount>";
	}
}
