package org.frustra.featherweight;

public class Player {
	public long karma = 0;
	public String name;

	public long firstJoin = 0, lastSeen = 0;

	public Player(String name) {
		this.name = name;
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
