package org.frustra.featherweight.commands;

import org.frustra.featherweight.Command;
import org.frustra.featherweight.Entity;

public class VoteKickCommand extends Command {

	public String getName() {
		return "votekick";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 1) {
			Command.execute("kick " + arguments[0]);
		} else {
			// TODO throw a ch
		}
	}

	public boolean hasPermission(Entity source) {
		return true;
	}
}
