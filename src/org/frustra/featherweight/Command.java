package org.frustra.featherweight;

import java.lang.reflect.Method;

import org.frustra.featherweight.hooks.CommandManagerClass;
import org.frustra.featherweight.hooks.ExecuteCommandMethod;
import org.frustra.featherweight.hooks.GetCommandNameMethod;
import org.frustra.featherweight.hooks.HelpCommandClass;
import org.frustra.featherweight.hooks.SendClientMessageMethod;
import org.frustra.filament.injection.annotations.OverrideMethod;
import org.frustra.filament.injection.annotations.ReplaceSuperClass;
import org.objectweb.asm.Type;

/**
 * The base class for every command. It gets dynamically rewritten to extend the
 * internal Minecraft command class, and proxies Minecraft's internal methods to
 * our own commands.
 */
@ReplaceSuperClass(hook = HelpCommandClass.class, field = "baseCommand")
public abstract class Command {
	@ReplaceSuperClass(hook = HelpCommandClass.class, field = "baseCommand")
	public Command() {}

	@OverrideMethod(hook = GetCommandNameMethod.class, field = "getName")
	private String getNameI() {
		return this.getName();
	}

	/**
	 * Gets the base name of the command, as it would be invoked by an entity.
	 * 
	 * @return the command's name
	 */
	public abstract String getName();

	/**
	 * Whether a particular entity has access to this command or not.
	 * 
	 * @param source the entity that invoked the command
	 * @return the accessibility of the command
	 */
	public abstract boolean hasPermission(Entity source);

	/**
	 * Called when a particular entity has invoked this command. Permissions
	 * will have already been checked.
	 * 
	 * @param source the entity that invoked the command
	 * @param arguments any arguments given by the player, split by spaces
	 */
	public abstract void execute(Entity source, String[] arguments);

	/**
	 * Executes a command string as the server.
	 * 
	 * @param command the command string
	 */
	public static void execute(String command) {
		execute(FeatherWeight.minecraftServer, command);
	}

	/**
	 * Executes a command string as if it were run by a particular source
	 * entity.
	 * 
	 * @param source the proxied entity source
	 * @param command the command string
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
	 * Sends some text to the server log.
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public static void log(String str, Object[] values) {
		respond(FeatherWeight.minecraftServer, str, values);
	}

	/**
	 * Sends some text to a particular target internal entity.
	 * 
	 * @param target the proxied entity target
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
