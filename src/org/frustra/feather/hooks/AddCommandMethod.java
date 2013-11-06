package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class AddCommandMethod extends MethodHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.equals(Hooks.getClass("CommandManager"));
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		return args.length == 1 && HookingHandler.compareType(args[0], "BaseCommand");
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		Hooks.set("CommandManager.addCommand", m);
	}
}
