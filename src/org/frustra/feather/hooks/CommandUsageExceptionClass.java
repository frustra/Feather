package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassProvider;
import org.frustra.filament.hooking.types.HookingPass;

@HookingPass(2)
public class CommandUsageExceptionClass extends ClassProvider {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.superName.equals(Hooks.getClassName("CommandException"));
	}

	public void complete(FilamentClassNode node) {
		Hooks.set("CommandUsageException", node);
	}
}
