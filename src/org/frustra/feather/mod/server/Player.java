package org.frustra.feather.mod.server;

public class Player extends Entity {
	public final String name;
	public double karma = 0;

	public long firstJoin = 0, lastSeen = 0;

	public Player(String name) {
		super(null);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public long seen() {
		return this.lastSeen = System.currentTimeMillis() / 1000;
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
