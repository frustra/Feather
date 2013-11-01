package org.frustra.featherweight.commands;

public abstract class Command {
	public abstract String getName();
	public abstract boolean execute(Object state, String[] arguments);
}
