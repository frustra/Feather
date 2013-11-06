package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandManagerClass extends ClassHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("Couldn't process command");
	}

	public void onComplete(CustomClassNode node) {
		Hooks.set("CommandManager", node);
		Hooks.set("BaseCommandManager", HookingHandler.getClassNode((String) node.interfaces.get(0)));
	}
}
