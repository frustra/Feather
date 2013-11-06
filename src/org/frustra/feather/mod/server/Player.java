package org.frustra.feather.mod.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.frustra.feather.mod.Bootstrap;
import org.frustra.filament.hooking.HookingHandler;

public class Player extends Entity {
	public final String name;
	public double karma = 0;

	public long firstJoin = 0, lastSeen = 0, lastKarmaUpdate;

	public Player(String name) {
		super(null);
		this.name = name;
	}

	/**
	 * Indicates that the player was just seen
	 * 
	 * @return the current time in seconds
	 */
	public long seen() {
		this.lastSeen = System.currentTimeMillis() / 1000;
		Bootstrap.server.updatePlayer(this);
		return this.lastSeen;
	}

	public double getKarma() {
		return karma;
	}

	/**
	 * Sets the player's karma, and updates them across the server and database
	 * 
	 * @param karma
	 */
	public void setKarma(double karma) {
		this.karma = karma;
		Bootstrap.server.updatePlayer(this);
	}

	/**
	 * Reaches into the server and determines if this player is an operator
	 * 
	 * @return true if the player is an operator
	 */
	public boolean isOperator() {
		try {
			if (_isOperatorMethod == null) {
				_playerHandlerField = HookingHandler.lookupField("MinecraftServer.playerHandler");
				_isOperatorMethod = HookingHandler.lookupMethod("PlayerHandler.isOperator");
			}
			Object playerHandler = _playerHandlerField.get(Bootstrap.minecraftServer);
			return (Boolean) _isOperatorMethod.invoke(playerHandler, getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Method _isOperatorMethod = null;
	private static Field _playerHandlerField = null;

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
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
