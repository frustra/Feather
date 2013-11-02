package org.frustra.featherweight;

import java.util.HashMap;

public class Server {
	private HashMap<String, Player> players;
	private Database db;

	/**
	 * Sets up the server and database
	 * 
	 * @throws Exception
	 */
	public Server() throws Exception {
		players = new HashMap<String, Player>();
		db = new Database();
	}

	/**
	 * Gracefully shuts down the server and database
	 */
	public void shutdown() {
		db.close();
	}

	/**
	 * Adds a player to the game.
	 * 
	 * @param name the player's username
	 * @return the player instance
	 */
	public Player loadPlayer(String name) {
		Player p = db.fetchPlayer(name);
		if (p != null) {
			p.seen();
			db.savePlayer(p);
			players.put(name, p);
		}
		return p;
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param name the player's username
	 */
	public void unloadPlayer(String name) {
		Player p = players.remove(name);
		if (p != null) {
			p.seen();
			db.savePlayer(p);
		}
	}

	/**
	 * Whether a particular player is online or not.
	 * 
	 * @param name the player's username
	 * @return true if the player is online
	 */
	public boolean isOnline(String name) {
		return players.containsKey(name);
	}
}
