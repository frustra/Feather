package org.frustra.feather;

import org.frustra.filament.injection.annotations.OverrideMethod;
import org.frustra.filament.injection.annotations.ProxyMethod;
import org.frustra.filament.injection.annotations.ReplaceSuperClass;

/**
 * The base class for every command. It gets dynamically rewritten to extend the
 * internal Minecraft command class, and proxies Minecraft's internal methods to
 * our own commands.
 */
@ReplaceSuperClass("HelpCommandClass.baseCommand")
public abstract class Command {
	@ReplaceSuperClass("HelpCommandClass.baseCommand")
	public Command() {}

	@OverrideMethod("GetCommandNameMethod.getName")
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
		execute(Feather.minecraftServer, command);
	}

	@ProxyMethod(
		classHook = "CommandManagerClass.commandManager",
		methodHook = "ExecuteCommandMethod.executeCommand"
	)
	private static native int executeI(Object instance, Object source, String command);

	/**
	 * Executes a command string as if it were run by a particular source
	 * entity.
	 * 
	 * @param source the proxied entity source
	 * @param command the command string
	 */
	public static void execute(Object source, String command) {
		executeI(Feather.commandManager, (Command) source, command);
	}

	/**
	 * Sends some text to the server log.
	 * 
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public static void log(String str, Object[] values) {
		respond(Feather.minecraftServer, str, values);
	}

	@ProxyMethod(
		classHook = "HelpCommandClass.baseCommand",
		methodHook = "SendClientMessageMethod.sendMessage"
	)
	private static native void respondI(Object target, String str, Object[] values);

	/**
	 * Sends some text to a particular target internal entity.
	 * 
	 * @param target the proxied entity target
	 * @param str the format string
	 * @param values the list of values to interpolate
	 */
	public static void respond(Object target, String str, Object[] values) {
		respondI(target, str, values);
	}
}
