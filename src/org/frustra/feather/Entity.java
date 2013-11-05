package org.frustra.feather;

import java.lang.reflect.Method;

import org.frustra.feather.hooks.RconEntityClass;
import org.frustra.feather.server.Player;
import org.frustra.filament.hooking.HookingHandler;

/**
 * Represents an entity that can issue commands. This is a helper class, which
 * wraps the underlying game entity and proxies methods dynamically.
 */
public class Entity {
	public Object instance;

	public Entity(Object instance) {
		this.instance = instance;
	}

	/**
	 * @return the entity's name, which will be the username if a player
	 */
	public String getName() {
		if (getNameMethod == null) getNameMethod = HookingHandler.lookupMethod(RconEntityClass.commandEntity, RconEntityClass.getEntityName);
		try {
			return (String) getNameMethod.invoke(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Method getNameMethod = null;

	/**
	 * Sends some text to this entity
	 * 
	 * @param str the text to send
	 */
	public void sendMessage(String str) {
		sendMessage(str, new Object[0]);
	}

	/**
	 * Sends some text to this entity
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public void sendMessage(String str, Object[] values) {
		try {
			if (commandMessage == null) {
				Class<?> cmd = Feather.loader.loadClass(Command.class.getName());
				commandMessage = cmd.getDeclaredMethod("sendMessage", new Class[] { Object.class, String.class, Object[].class });
			}
			commandMessage.invoke(null, instance, str, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Method commandMessage = null;

	/**
	 * Gets the Player this entity represents, if it is a player
	 * 
	 * @return the player instance
	 */
	public Player getPlayer() {
		return Feather.server.getPlayer(this.getName());
	}
}
