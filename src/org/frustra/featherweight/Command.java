package org.frustra.featherweight;

import java.lang.reflect.Method;

import org.frustra.featherweight.hooks.CommandManagerClass;
import org.frustra.featherweight.hooks.ExecuteCommandMethod;
import org.frustra.featherweight.hooks.HelpCommandClass;
import org.frustra.featherweight.hooks.SendClientMessageMethod;
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
				Type[] args = Type.getArgumentTypes(ExecuteCommandMethod.executeCommand.desc);

				Class<?> commandManagerClass = loader.loadClass(CommandManagerClass.commandManager.name.replace('/', '.'));
				Class<?> commandEntityClass = loader.loadClass(args[0].getClassName());

				executeCommandMethod = commandManagerClass.getDeclaredMethod(ExecuteCommandMethod.executeCommand.name, new Class[] { commandEntityClass, String.class });
				executeCommandMethod.setAccessible(true);
			}
			executeCommandMethod.invoke(FeatherWeight.commandManager, new Object[] { source, command });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends some output to the server log.
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public static void log(String str, Object[] values) {
		respond(FeatherWeight.minecraftServer, str, values);
	}

	/**
	 * Sends some output to a particular target internal entity
	 * 
	 * @param target
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public static void respond(Object target, String str, Object[] values) {
		try {
			if (sendClientMethod == null) {
				CustomClassLoader loader = FeatherWeight.loader;
				Type[] args = Type.getArgumentTypes(SendClientMessageMethod.sendMessage.desc);

				Class<?> baseCommandClass = loader.loadClass(HelpCommandClass.baseCommand.name.replace('/', '.'));
				Class<?> commandEntityClass = loader.loadClass(args[0].getClassName());

				sendClientMethod = baseCommandClass.getDeclaredMethod(SendClientMessageMethod.sendMessage.name, new Class[] { commandEntityClass, String.class, Object[].class });
				sendClientMethod.setAccessible(true);
			}
			sendClientMethod.invoke(null, new Object[] { target, str, values });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Method executeCommandMethod = null;
	private static Method sendClientMethod = null;
}
