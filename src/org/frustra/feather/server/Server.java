package org.frustra.feather.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.frustra.feather.server.logging.LogManager;
import org.frustra.feather.server.votifer.VoteListener;
import org.frustra.feather.server.votifer.VotifierLite;
import org.frustra.feather.server.voting.KickVote;

public class Server implements VoteListener {
	private Database db;
	private HashMap<String, Player> players = new HashMap<String, Player>();
	private ArrayList<PlayerListener> playerListeners = new ArrayList<PlayerListener>();
	private VotifierLite votifier;

	public HashMap<Player, KickVote> activeKickVotes = new HashMap<Player, KickVote>();

	/**
	 * Sets up the server and database
	 * 
	 * @throws Exception
	 */
	public Server() throws Exception {
		db = new Database();
		votifier = new VotifierLite();
		votifier.init();
		votifier.addVoteListener(this);

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new UpdateKarmaTask(), 1, 1, TimeUnit.MINUTES);
	}

	public void ready() {
		Command.execute("scoreboard objectives add karma dummy K");
		Command.execute("scoreboard objectives setdisplay list karma");
		Command.execute("scoreboard objectives setdisplay belowName karma");

		votifier.start();
	}

	/**
	 * Gracefully shuts down the server and database
	 */
	public void shutdown() {
		votifier.running = false;
		db.close();
	}

	/**
	 * Gets all in-game players.
	 * 
	 * @return the collection of players
	 */
	public Collection<Player> getPlayers() {
		return players.values();
	}

	/**
	 * Gets the current number of players online.
	 * 
	 * @return the number of online players
	 */
	public int onlinePlayers() {
		return players.size();
	}

	/**
	 * Gets an in-game player by name.
	 * 
	 * @param name the player's username
	 * @return the player instance
	 */
	public Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	/**
	 * Gets a player by name.
	 * 
	 * @param name the player's username
	 * @return the player instance
	 */
	public Player fetchPlayer(String name) {
		Player ret = getPlayer(name);
		if (ret == null) {
			ret = db.fetchPlayer(name);
		}
		return ret;
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
			p = db.createPlayer(name);
			if (p != null) {
				p.seen();
				p.lastKarmaUpdate = p.lastSeen;
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

	public void updatePlayer(Player player) {
		db.savePlayer(player);
		if (player.instance != null) Command.execute("scoreboard players set " + player.getName() + " karma " + (long) player.getKarma());
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
	 * Adds the specified player listener to receive player join and leave
	 * events.
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

	public void voteReceived(String service, String username, String address, String timestamp) {
		Player p = getPlayer(username);
		if (p == null) p = db.createPlayer(username);
		LogManager.getLogger().info("Received vote from player: " + username);
		if (p.instance != null) {
			Command.execute("tellraw " + p.name + " {\"text\":\"You have received 0.5 karma for voting on " + service + "\",\"color\":\"green\"}");
			for (Player p2 : getPlayers()) {
				if (p2.equals(p)) continue;
				Command.execute("tellraw " + p2.name + " {\"text\":\"" + p.name + " has received 0.5 karma for voting on " + service + "\",\"color\":\"green\"}");
			}
		}
		p.setKarma(p.karma + 0.5);
	}
}
