package org.frustra.feather.mod.server;

import java.lang.reflect.Method;

import org.frustra.feather.Feather;
import org.frustra.feather.hooks.HelpCommandClass;
import org.frustra.feather.hooks.RconEntityClass;
import org.frustra.feather.hooks.SendClientMessageMethod;
import org.frustra.filament.hooking.HookingHandler;

/**
 * Represents an entity that can issue commands. This is a helper class, which
 * wraps the underlying game entity and proxies methods dynamically.
 */
public class Entity {
	public Object instance;

	private static Method _getName = null;
	private static Method _sendMessage = null;

	public Entity(Object instance) {
		this.instance = instance;
	}

	/**
	 * @return the entity's name, which will be the username if a player
	 */
	public String getName() {
		try {
			if (_getName == null) _getName = HookingHandler.lookupMethod(RconEntityClass.commandEntity, RconEntityClass.getEntityName);
			return (String) _getName.invoke(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
			if (_sendMessage == null) _sendMessage = HookingHandler.lookupMethod(HelpCommandClass.baseCommand, SendClientMessageMethod.sendMessage);
			_sendMessage.invoke(null, instance, str, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the Player this entity represents, if it is a player
	 * 
	 * @return the player instance
	 */
	public Player getPlayer() {
		return Feather.server.getPlayer(this.getName());
	}
}
