package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandManagerClass extends ClassHook implements HookingPassOne {
	public boolean match(FilamentClassNode node) {
		return node.constants.contains("Couldn't process command");
	}

	public void onComplete(FilamentClassNode node) {
		Hooks.set("CommandManager", node);
		Hooks.set("BaseCommandManager", HookUtil.getClassNode((String) node.interfaces.get(0)));
	}
}
