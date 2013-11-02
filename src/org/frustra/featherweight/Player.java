package org.frustra.featherweight;

public class Player {
	public long karma = 0;
	public String name;

	public long firstJoin, lastSeen;

	public Player(String name) {
		this.name = name;
	}

	public long seen() {
		return this.lastSeen = System.currentTimeMillis() / 1000;
	}
}
