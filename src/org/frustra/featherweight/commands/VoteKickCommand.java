package org.frustra.featherweight.commands;

import org.frustra.featherweight.Command;
import org.frustra.featherweight.Entity;
import org.frustra.featherweight.FeatherWeight;
import org.frustra.featherweight.KickVote;
import org.frustra.featherweight.Player;

public class VoteKickCommand extends Command {

	public String getName() {
		return "votekick";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 1) {
			Player target = FeatherWeight.server.getPlayer(arguments[1]);
			KickVote vote = FeatherWeight.server.activeKickVotes.get(target);
			if (vote == null) {
				if (source.getPlayer().karma > 0) {
					vote = FeatherWeight.server.activeKickVotes.put(target, new KickVote(target));
				} else {
					source.respond("Not enough karma!");
					return;
				}
			}
			if (vote.addVote(source.getPlayer())) {
				Command.execute("kick " + target.name);
			} else {
				source.respond("Still need more votes to kick %s.", new Object[] { target.name });
			}
		} else {
			// TODO throw a ch
			source.respond("Invalid usage.");
		}
	}

	public boolean hasPermission(Entity source) {
		return true;
	}
}
