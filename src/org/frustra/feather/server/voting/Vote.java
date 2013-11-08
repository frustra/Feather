package org.frustra.feather.server.voting;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Player;

public abstract class Vote {
	public double score = 0, participating = 0, eligibleKarma, threshold;
	public long timeout;
	public Set<Player> voters;
	public Set<Player> eligibleVoters;

	private boolean completed = false;

	public Vote(double threshold, long timeout) {
		this.threshold = threshold;
		this.timeout = timeout;
		voters = new HashSet<Player>();
		eligibleVoters = new HashSet<Player>();
		for (Player p : Bootstrap.server.getPlayers()) {
			if (p.karma > 0) {
				eligibleVoters.add(p);
				eligibleKarma += p.getKarma();
			}
		}

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.schedule(new Runnable() {
			public void run() {
				if (!completed) {
					timeout();
				}
			}
		}, timeout, TimeUnit.SECONDS);
	}

	/**
	 * Adds a player's vote to this vote session
	 * 
	 * @param p the voting player
	 * @param agree if the player voted yes or no
	 * @return true if the vote was counted
	 */
	public boolean addVote(Player p, boolean agree) {
		if (completed) return false;
		if (p.karma > 0 && eligibleVoters.contains(p) && voters.add(p)) {
			score += agree ? p.karma : -p.karma;
			participating += p.karma;
			if (hasPassed()) {
				completed = true;
				passed();
			} else if (hasFailed()) {
				completed = true;
				failed();
			}
			return true;
		}
		return false;
	}

	public boolean hasPassed() {
		// Treat the unvoted population as against the vote, until they vote
		return (score - (eligibleKarma - participating)) > threshold;
	}

	public boolean hasFailed() {
		// Treat the unvoted population as with the vote, until they vote
		return (score + (eligibleKarma - participating)) <= threshold;
	}

	public boolean hasVoted(Player p) {
		return voters.contains(p.name);
	}

	public abstract void passed();

	public abstract void failed();

	public abstract void timeout();
}
