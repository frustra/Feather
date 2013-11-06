package org.frustra.feather.mod.server;

import org.frustra.feather.mod.Bootstrap;

public class Player extends Entity {
	public final String name;
	public double karma = 0;

	public long firstJoin = 0, lastSeen = 0, lastKarmaUpdate;

	public Player(String name) {
		super(null);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

	public long seen() {
		this.lastSeen = System.currentTimeMillis() / 1000;
		Bootstrap.server.updatePlayer(this);
		return this.lastSeen;
	}

	public double getKarma() {
		return karma;
	}

	public void setKarma(double karma) {
		this.karma = karma;
		Bootstrap.server.updatePlayer(this);
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof Player) {
			return name.equals(((Player) other).name);
		}
		return false;
	}
}
