package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassProvider;
import org.frustra.filament.hooking.types.HookingPass;

@HookingPass(1)
public class CommandManagerClass extends ClassProvider {
	public boolean match(FilamentClassNode node) {
		return node.containsConstant("Couldn't process command");
	}

	public void complete(FilamentClassNode node) {
		Hooks.set("CommandManager", node);
		Hooks.set("BaseCommandManager", HookUtil.getClassNode((String) node.interfaces.get(0)));
	}
}
