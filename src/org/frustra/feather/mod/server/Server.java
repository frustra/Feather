package org.frustra.feather.mod.server;

import java.util.ArrayList;
import java.util.HashMap;

import org.frustra.feather.mod.voting.KickVote;

public class Server {
	private Database db;
	private HashMap<String, Player> players = new HashMap<String, Player>();
	private ArrayList<PlayerListener> playerListeners = new ArrayList<PlayerListener>();

	public HashMap<Player, KickVote> activeKickVotes = new HashMap<Player, KickVote>();

	/**
	 * Sets up the server and database
	 * 
	 * @throws Exception
	 */
	public Server() throws Exception {
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

	/**
	 * Gets the total karma of all players on the server, excluding players with
	 * negative karma
	 * 
	 * @return the total karma
	 */
	public double totalKarma() {
		double total = 0;
		for (Player p : players.values()) {
			if (p.karma > 0) total += p.karma;
		}
		return total;
	}

	/**
	 * Gets the current message of the day for a given player
	 * 
	 * @param player the player
	 * @return the motd for the player
	 */
	public String getMotd(Player player) {
		return "Welcome, " + player.getName() + "!";
	}

	/**
	 * Adds the specified player listener to receive player join and leave events.
	 * 
	 * @param listener the player listener
	 */
	public void addPlayerListener(PlayerListener listener) {
		playerListeners.add(listener);
	}

	/**
	 * Reamoves a previously added player listener.
	 * 
	 * @param listener the player listener
	 */
	public void removePlayerListener(PlayerListener listener) {
		playerListeners.remove(listener);
	}

	public void playerJoined(Entity entity) {
		Player player = loadPlayer(entity.getName());
		player.instance = entity.instance;

		player.sendMessage(getMotd(player));
		for (PlayerListener listener : playerListeners) {
			listener.playerJoined(player);
		}
	}

	public void playerLeft(Entity entity) {
		Player player = getPlayer(entity.getName());

		if (player != null) {
			for (PlayerListener listener : playerListeners) {
				listener.playerLeft(player);
			}
			unloadPlayer(entity.getName());
		}
	}
}
