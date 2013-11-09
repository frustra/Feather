package org.frustra.feather.server.commands;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.CommandException;
import org.frustra.feather.server.CommandUsageException;
import org.frustra.feather.server.Entity;
import org.frustra.feather.server.Player;

public class OpUpCommand extends Command {
	public String getName() {
		return "opup";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 0) {
			Command.execute("op " + source.getName());
			source.sendMessage("You now have op");
		} else if (arguments.length == 1) {
			Player target = Bootstrap.server.fetchPlayer(arguments[1]);
			if (target == null) {
				throw new CommandException(target + " hasn't played here");
			} else {
				target.makeAllowedOperator();
				target.sendMessage("You now have access to /opup");
				source.sendMessage(target + " now has access to /opup");
			}
		} else throw new CommandUsageException(this);
	}

	public boolean hasPermission(Entity source) {
		Player p = source.getPlayer();
		return p == null || p.isAllowedOperator();
	}

	public String getUsage(Entity source) {
		return "/opup [player]";
	}
}
