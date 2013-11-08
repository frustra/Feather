package org.frustra.feather.server.voting;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.Player;

public class KickVote extends Vote {
	public Player target;

	public KickVote(Player target) {
		super(0, 120);
		this.target = target;
		addVote(target, false);
	}

	public boolean addVote(Player p, boolean agree) {
		if (!super.addVote(p, agree)) return false;
		if (agree && !hasPassed()) {
			Command.execute("tellraw @a {\"text\":\"" + (voters.size() - 1) + " of " + (eligibleVoters.size() - 1) + " eligible players have responded to the vote to kick " + target.name + "\",\"color\":\"blue\"}");
		}
		return true;
	}

	public void passed() {
		// Lose between 1 and 10 karma, depending on the proportion of kick agreement
		double lostKarma = Math.ceil(score / eligibleKarma * 10);
		target.setKarma(target.karma - lostKarma);

		Command.execute("kick " + target.name + " You have been voted off the server, and lost " + lostKarma + " karma.");
		Command.execute("tellraw @a {\"text\":\"" + target.name + " has been voted off the server, and lost " + lostKarma + " karma\",\"color\":\"blue\"}");
		finish();
	}

	public void failed() {
		Command.execute("tellraw @a {\"text\":\"The vote to kick " + target.name + " has failed\",\"color\":\"blue\"}");
		finish();
	}

	public void timeout() {
		Command.execute("tellraw @a {\"text\":\"Not enough players voted to kick " + target.name + "\",\"color\":\"blue\"}");
		finish();
	}

	public void finish() {
		Bootstrap.server.activeKickVotes.remove(target);
	}
}
