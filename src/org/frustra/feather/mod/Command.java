package org.frustra.feather.mod;

import org.frustra.feather.mod.server.Entity;
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

	/**
	 * Gets the base name of the command, as it would be invoked by an entity.
	 * 
	 * @return the command's name
	 */
	public abstract String getName();

	@OverrideMethod("HelpCommandClass.getCommandName")
	private String _getName() {
		return this.getName();
	}

	/**
	 * Whether a particular entity has access to this command or not.
	 * 
	 * @param source the entity that invoked the command
	 * @return the accessibility of the command
	 */
	public abstract boolean hasPermission(Entity source);

	@OverrideMethod("HasCommandPermissionMethod.hasPermission")
	private boolean _hasPermission(Object source) {
		return hasPermission(new Entity(source));
	}

	/**
	 * Called when a particular entity has invoked this command. Permissions
	 * will have already been checked.
	 * 
	 * @param source the entity that invoked the command
	 * @param arguments any arguments given by the player, split by spaces
	 */
	public abstract void execute(Entity source, String[] arguments);

	/**
	 * Gets the usage string of the command, as it would be displayed by the help command.
	 * 
	 * @return the command's usage string
	 */
	public abstract String getUsage(Entity source);

	@OverrideMethod("GetCommandUsageMethod.getUsage")
	private String _getUsage(Object source) {
		return getUsage(new Entity(source));
	}

	/**
	 * Executes a command string as the server.
	 * 
	 * @param command the command string
	 */
	public static void execute(String command) {
		execute(Bootstrap.minecraftServer, command);
	}

	@OverrideMethod("HandleExecuteCommandMethod.handleExecute")
	private void _execute(Entity source, String[] arguments) {
		execute(new Entity(source), arguments);
	}

	/**
	 * Executes a command string as if it were run by a particular source
	 * entity.
	 * 
	 * @param source the proxied entity source
	 * @param command the command string
	 */
	public static void execute(Object source, String command) {
		_execute(Bootstrap.commandManager, source, command);
	}

	@ProxyMethod(classHook = "CommandManagerClass.commandManager", methodHook = "ExecuteCommandMethod.executeCommand")
	private static native int _execute(Object instance, Object source, String command);

	/**
	 * Add a command to the command manager to it can be executed.
	 * 
	 * @param command the command object to be added
	 */
	public static void addCommand(Command command) {
		_addCommand(Bootstrap.commandManager, command);
	}

	@ProxyMethod(classHook = "CommandManagerClass.commandManager", methodHook = "AddCommandMethod.addCommand")
	private static native Object _addCommand(Object instance, Object command);
}
