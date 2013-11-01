package org.frustra.featherweight;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

public abstract class Command {
	/**
	 * @return the command's name, as used by a player
	 */
	public abstract String getName();

	/**
	 * @param source the entity that invoked the command
	 * @return whether to allow execution of the command or not
	 */
	public abstract boolean hasPermission(Entity source);

	/**
	 * @param source the entity that invoked the command
	 * @param arguments any arguments given by the player, split by spaces
	 */
	public abstract void execute(Entity source, String[] arguments);

	/**
	 * Executes a command string as the server
	 * 
	 * @param command
	 */
	public static void execute(String command) {
		execute(FeatherWeight.minecraftServer, command);
	}

	/**
	 * Executes a command string as a particular source
	 * 
	 * @param source
	 * @param command
	 */
	public static void execute(Object source, String command) {
		try {
			if (executeCommandMethod == null) {
				CustomClassLoader loader = FeatherWeight.loader;
				Class<?> commandManagerClass = loader.loadClass(loader.commandManagerClass.name.replace('/', '.'));
				Type[] args = Type.getArgumentTypes(loader.executeCommandMethod.desc);
				executeCommandMethod = commandManagerClass.getDeclaredMethod(loader.executeCommandMethod.name, loader.loadClass(args[0].getClassName()), String.class);
				executeCommandMethod.setAccessible(true);
			}
			executeCommandMethod.invoke(FeatherWeight.commandManager, new Object[] { source, command });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Method executeCommandMethod = null;
}
