package org.frustra.featherweight;

/**
 * Represents an entity that can issue commands.
 * This is a helper class, which wraps the underlying game entity and proxies methods dynamically.
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
}
