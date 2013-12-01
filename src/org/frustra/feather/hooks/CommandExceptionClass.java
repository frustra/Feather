package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassProvider;
import org.frustra.filament.hooking.types.HookingPass;

@HookingPass(1)
public class CommandExceptionClass extends ClassProvider {
	public boolean match(FilamentClassNode node) {
		return node.containsConstant("commands.generic.snytax"); // Not a typo.
	}

	public void complete(FilamentClassNode node) {
		Hooks.set("CommandException", node);
	}
}
