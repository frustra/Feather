package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.MethodProvider;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(2)
public class AddCommandMethod extends MethodProvider {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.matches("CommandManager");
	}

	public boolean match(FilamentClassNode node, MethodNode m) throws BadHookException {
		Type[] args = Type.getArgumentTypes(m.desc);
		return args.length == 1 && HookUtil.compareType(args[0], "BaseCommand");
	}

	public void complete(FilamentClassNode node, MethodNode m) {
		Hooks.set("CommandManager.addCommand", m);
	}
}
