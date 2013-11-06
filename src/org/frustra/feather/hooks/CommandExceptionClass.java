package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandExceptionClass extends ClassHook implements HookingPassOne {
	public static CustomClassNode commandException = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.generic.snytax"); // Not a typo.
	}

	public void reset() {
		super.reset();
		commandException = null;
	}

	public void onComplete(CustomClassNode node) {
		commandException = node;
		if (Feather.debug) {
			System.out.println("Command Exception Class: " + commandException.name);
		}
	}
}
