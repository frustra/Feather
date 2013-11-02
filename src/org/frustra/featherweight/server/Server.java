package org.frustra.featherweight.server;

import java.util.HashMap;

public class Server {
	private Database db;

	private HashMap<String, Player> players;
	public HashMap<Player, KickVote> activeKickVotes;
	public HashMap<Player, BanVote> activeBanVotes;

	/**
	 * Sets up the server and database
	 * 
	 * @throws Exception
	 */
	public Server() throws Exception {
		players = new HashMap<String, Player>();
		activeKickVotes = new HashMap<Player, KickVote>();
		activeBanVotes = new HashMap<Player, BanVote>();
		db = new Database();
	}

	/**
	 * Gracefully shuts down the server and database
	 */
	public void shutdown() {
		db.close();
	}

	/**
	 * Gets a player currently in the game.
	 * 
	 * @param name the player's username
	 * @return the player instance
	 */
	public Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	/**
	 * Adds a player to the game.
	 * 
	 * @param name the player's username
	 * @return the player instance
	 */
	public Player loadPlayer(String name) {
		Player p = getPlayer(name);
		if (p == null) {
			p = db.fetchPlayer(name);
			if (p != null) {
				p.seen();
				db.savePlayer(p);
				players.put(name.toLowerCase(), p);
			}
		}
		return p;
	}

	/**
	 * Removes a player from the game.
	 * 
	 * @param name the player's username
	 */
	public void unloadPlayer(String name) {
		Player p = players.remove(name.toLowerCase());
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
		return players.containsKey(name.toLowerCase());
	}
}
