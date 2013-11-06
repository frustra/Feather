package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassTwo;

public class CommandUsageExceptionClass extends ClassHook implements HookingPassTwo {
	public static CustomClassNode commandUsageException = null;

	public boolean match(CustomClassNode node) {
		return node.superName.equals(CommandExceptionClass.commandException.name);
	}

	public void reset() {
		super.reset();
		commandUsageException = null;
	}

	public void onComplete(CustomClassNode node) {
		commandUsageException = node;
		if (Feather.debug) {
			System.out.println("Command Usage Exception Class: " + commandUsageException.name);
		}
	}
}
