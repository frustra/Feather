package org.frustra.feather.server.voting;

import org.frustra.feather.server.Bootstrap;
import org.frustra.feather.server.Command;
import org.frustra.feather.server.Player;

public class KickVote extends Vote {
	public Player target;

	public KickVote(Player target) {
		super(0);
		this.target = target;
		addVote(target, false);
	}
	
	public boolean addVote(Player p, boolean agree) {
		if (!super.addVote(p, agree)) return false;
		if (agree) {
			if (hasPassed()) {
				Command.execute("tellraw @a {\"text\":\"" + target.name + " has been voted off the server\",\"color\":\"blue\"}");
			} else Command.execute("tellraw @a {\"text\":\"" + (voters.size() - 1) + " of " + (Bootstrap.server.onlinePlayers() - 1) + " players have responded to the vote kick against " + target.name + "\",\"color\":\"blue\"}");
		}
		return true;
	}

	public void passed() {
		Command.execute("kick " + target.name + " You have been voted off the server.");
	}

	public void failed() {}

	public void timeout() {}
}
