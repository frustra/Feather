package org.frustra.featherweight.commands;

public abstract class Command {
	public abstract String getName();
	public abstract boolean hasPermission(Object state);
	public abstract void execute(Object state, String[] arguments);
}
