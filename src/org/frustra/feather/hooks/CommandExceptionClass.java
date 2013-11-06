package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandExceptionClass extends ClassHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.generic.snytax"); // Not a typo.
	}

	public void onComplete(CustomClassNode node) {
		Hooks.set("CommandException", node);
	}
}
