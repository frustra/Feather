package org.frustra.feather.server;

public class UpdateKarmaTask implements Runnable {
	public void run() {
		long currentTime = System.currentTimeMillis() / 1000;
		for (Player p : Bootstrap.server.getPlayers()) {
			p.karma += (currentTime - p.lastKarmaUpdate) / 3600.0;
			p.playTime += (currentTime - p.lastKarmaUpdate);
			p.lastKarmaUpdate = currentTime;
			p.seen();
		}
	}
}
