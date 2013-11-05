package org.frustra.feather.voting;

import org.frustra.feather.Command;
import org.frustra.feather.server.Player;

public class KickVote extends Vote {
	public static final double Threshold = 0;
	public Player target;

	public KickVote(Player target) {
		super(KickVote.Threshold);
		addVote(target, false);
	}

	public void passed() {
		Command.execute("kick " + target.name);
	}

	public void failed() {}

	public void timeout() {}
}
