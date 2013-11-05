package org.frustra.feather.mod.voting;

import java.util.HashSet;
import java.util.Set;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.feather.mod.server.Player;

public abstract class Vote {
	public double score = 0, participating = 0, threshold;
	public Set<Player> voters;

	public Vote(double threshold) {
		this.threshold = threshold;
		this.voters = new HashSet<Player>();
	}

	/**
	 * Adds a player's vote to this vote session
	 * 
	 * @param p the voting player
	 * @param agree if the player voted yes or no
	 * @return true if the vote has passed
	 */
	public boolean addVote(Player p, boolean agree) {
		if (p.karma > 0 && voters.add(p)) {
			score += agree ? -p.karma : p.karma;
			participating += p.karma;
			if (hasPassed()) {
				passed();
				return true;
			}
		}
		return false;
	}

	public boolean hasPassed() {
		// Treat the unvoted population as against the vote, until they vote
		return (score + Bootstrap.server.totalKarma() - participating) < threshold;
	}

	public boolean hasVoted(Player p) {
		return voters.contains(p.name);
	}

	public abstract void passed();

	public abstract void failed();

	public abstract void timeout();
}
