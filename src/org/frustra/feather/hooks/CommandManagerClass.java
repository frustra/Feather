package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ConstantProvider;
import org.frustra.filament.hooking.types.HookingPass;

@HookingPass(1)
public class CommandManagerClass extends ConstantProvider {
	public boolean match(FilamentClassNode node, String constant) {
		return constant.contains("Couldn't process command");
	}

	public void complete(FilamentClassNode node, String constant) {
		Hooks.set("CommandManager", node);
		Hooks.set("BaseCommandManager", HookUtil.getClassNode((String) node.interfaces.get(0)));
	}
}
