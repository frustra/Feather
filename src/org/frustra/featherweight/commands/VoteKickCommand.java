package org.frustra.featherweight.commands;

import org.frustra.featherweight.Command;
import org.frustra.featherweight.Entity;
import org.frustra.featherweight.FeatherWeight;
import org.frustra.featherweight.server.KickVote;
import org.frustra.featherweight.server.Player;

public class VoteKickCommand extends Command {

	public String getName() {
		return "votekick";
	}

	public void execute(Entity source, String[] arguments) {
		if (arguments.length == 1) {
			Player sourcePlayer = source.getPlayer();
			if (sourcePlayer.karma <= 0) {
				source.respond("Not enough karma!");
				return;
			}

			Player target = FeatherWeight.server.getPlayer(arguments[1]);
			KickVote vote = FeatherWeight.server.activeKickVotes.get(target);
			if (vote == null) {
				vote = FeatherWeight.server.activeKickVotes.put(target, new KickVote(target));
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
