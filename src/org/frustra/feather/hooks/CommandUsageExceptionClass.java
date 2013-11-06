package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassTwo;

public class CommandUsageExceptionClass extends ClassHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.superName.equals(Hooks.getClassName("CommandException"));
	}

	public void onComplete(CustomClassNode node) {
		Hooks.set("CommandUsageException", node);
	}
}
