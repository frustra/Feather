package org.frustra.feather.mod.voting;

import org.frustra.feather.mod.commands.Command;
import org.frustra.feather.mod.server.Player;

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
