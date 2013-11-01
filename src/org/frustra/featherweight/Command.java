package org.frustra.featherweight;

public abstract class Command {
	/**
	 * @return the command's name, as used by a player
	 */
	public abstract String getName();
	public abstract boolean a(Object source);

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

}
