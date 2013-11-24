package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandExceptionClass extends ClassHook implements HookingPassOne {
	public boolean match(FilamentClassNode node) {
		return node.constants.contains("commands.generic.snytax"); // Not a typo.
	}

	public void onComplete(FilamentClassNode node) {
		Hooks.set("CommandException", node);
	}
}
