package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class ExecuteCommandMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode executeCommand = null;

	public boolean match(CustomClassNode node) {
		return node.equals(CommandManagerClass.commandManager);
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Type ret = Type.getReturnType(m.desc);
		return ret.equals(Type.INT_TYPE) && args.length == 2 && args[0].getInternalName().equals(RconEntityClass.commandEntity.name) && args[1].equals(Type.getType(String.class));
	}

	public void reset() {
		super.reset();
		executeCommand = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		executeCommand = m;
		if (FeatherWeight.debug) {
			System.out.println("Execute Command Method: " + executeCommand.name + executeCommand.desc);
		}
	}
}
