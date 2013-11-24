package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassTwo;

public class CommandUsageExceptionClass extends ClassHook implements HookingPassTwo {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.superName.equals(Hooks.getClassName("CommandException"));
	}

	public void onComplete(FilamentClassNode node) {
		Hooks.set("CommandUsageException", node);
	}
}
