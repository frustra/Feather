package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class HandleExecuteCommandMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode handleExecute = null;

	public boolean match(CustomClassNode node) {
		return node.equals(HelpCommandClass.baseCommandInterface);
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Type ret = Type.getReturnType(m.desc);
		return args.length == 2 && args[0].getInternalName().equals(RconEntityClass.commandEntity.name) && args[1].equals(Type.getType(String[].class)) && ret.equals(Type.VOID_TYPE);
	}

	public void reset() {
		super.reset();
		handleExecute = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		handleExecute = m;
		if (Feather.debug) {
			System.out.println("Handle Execute Command Method: " + handleExecute.name + handleExecute.desc);
		}
	}
}
