package org.frustra.feather.voting;

import java.util.HashSet;
import java.util.Set;

import org.frustra.feather.server.Player;

public class Vote {
	public long score = 0, threshold;
	public Set<String> voters;

	public Vote(long threshold) {
		this.threshold = threshold;
		this.voters = new HashSet<String>();
	}

	/**
	 * Adds a player's vote to this vote session
	 * 
	 * @param p the voting player
	 * @return true if the vote has passed
	 */
	public boolean addVote(Player p) {
		if (p.karma > 0 && voters.add(p.name)) {
			score += p.karma;
			if (hasPassed()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPassed() {
		return score >= threshold;
	}

	public boolean hasVoted(Player p) {
		return voters.contains(p.name);
	}
}
