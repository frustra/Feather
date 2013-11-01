package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class HasCommandPermissionMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode hasPermission = null;
	
	public boolean match(CustomClassNode node) {
		return node.equals(HelpCommandClass.baseCommandInterface);
	}
	
	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Type ret = Type.getReturnType(m.desc);
		return args.length == 1 && args[0].getInternalName().equals(RconEntityClass.commandEntity.name) && ret.equals(Type.BOOLEAN_TYPE);
	}
	
	public void reset() {
		super.reset();
		hasPermission = null;
	}
	
	public void onComplete(CustomClassNode node, MethodNode m) {
		hasPermission = m;
		if (FeatherWeight.debug) {
			System.out.println("Has Command Permission Method: " + hasPermission.name + hasPermission.desc);
		}
	}
}
