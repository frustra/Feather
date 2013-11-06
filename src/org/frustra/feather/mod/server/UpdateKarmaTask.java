package org.frustra.feather.mod.server;

import org.frustra.feather.mod.Bootstrap;

public class UpdateKarmaTask implements Runnable {
	public void run() {
		long currentTime = System.currentTimeMillis() / 1000;
		for (Player p : Bootstrap.server.getPlayers()) {
			p.karma += (currentTime - p.lastKarmaUpdate) / 3600.0;
			p.lastKarmaUpdate = currentTime;
			p.seen();
		}
	}
}
