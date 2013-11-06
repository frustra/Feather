package org.frustra.feather.mod.voting;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.feather.mod.Command;
import org.frustra.feather.mod.server.Player;

public class KickVote extends Vote {
	public static final double Threshold = 0;
	public Player target;

	public KickVote(Player target) {
		super(KickVote.Threshold);
		addVote(target, false);
	}
	
	public boolean addVote(Player p, boolean agree) {
		if (!super.addVote(p, agree)) return false;
		if (agree) Command.execute("tellraw @a {\"text\":\"" + voters.size() + " of " + Bootstrap.server.onlinePlayers() + " players have responded to the vote kick against " + target.name + "\",\"color\":\"dark_blue\"}");
		return true;
	}

	public void passed() {
		Command.execute("kick " + target.name);
	}

	public void failed() {}

	public void timeout() {}
}
