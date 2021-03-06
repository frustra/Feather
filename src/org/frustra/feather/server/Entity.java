package org.frustra.feather.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;

/**
 * Represents an entity that can issue commands. This is a helper class, which
 * wraps the underlying game entity and proxies methods dynamically.
 */
public class Entity {
	public Object instance;

	private static Method _getName = null;
	private static Method _sendMessage = null;
	private static Constructor<?> _textComponentConstructor = null;

	public Entity(Object instance) {
		this.instance = instance;
	}

	/**
	 * @return the entity's name, which will be the username if a player
	 */
	public String getName() {
		try {
			if (_getName == null) _getName = HookUtil.lookupMethod(Hooks.getClass("CommandEntity"), Hooks.getMethod("CommandEntity.getName"));
			return (String) _getName.invoke(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Sends some text to this entity
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public void sendMessage(String str, Object... values) {
		try {
			if (_textComponentConstructor == null) {
				Class<?> cls = HookUtil.lookupClass("TextComponent");
				_textComponentConstructor = cls.getConstructor(String.class);
				_textComponentConstructor.setAccessible(true);
			}
			Object textComponent = _textComponentConstructor.newInstance(String.format(str, values));
			if (_sendMessage == null) _sendMessage = HookUtil.lookupMethod("CommandEntity.sendMessage");
			_sendMessage.invoke(instance, textComponent);
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
		return Bootstrap.server.getPlayer(this.getName());
	}

	/**
	 * Returns whether the entity is an operator or not. This will only return
	 * false if the entity is a player.
	 * 
	 * @return true if the entity is an operator
	 */
	public boolean isOperator() {
		Player p = getPlayer();
		if (p == null) return true;
		return p.isOperator();
	}
}
