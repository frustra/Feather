package org.frustra.featherweight;

/**
 * Represents an entity that can issue commands. This is a helper class, which
 * wraps the underlying game entity and proxies methods dynamically.
 */
public class Entity {
	public Object entity;

	public Entity(Object entity) {
		this.entity = entity;
	}

	/**
	 * @return the entity's name, which will be the username if a player
	 */
	public String getName() {
		// Dynamically generated method
		return null;
	}

	/**
	 * Sends some text to this entity
	 * 
	 * @param str the text to send
	 */
	public void respond(String str) {
		respond(str, new Object[0]);
	}

	/**
	 * Sends some text to this entity
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public void respond(String str, Object[] values) {
		Command.respond(this.entity, str, values);
	}

	/**
	 * Gets the Player this entity represents, if it is a player
	 * 
	 * @return the player instance
	 */
	public Player getPlayer() {
		return FeatherWeight.server.getPlayer(this.getName());
	}
}
