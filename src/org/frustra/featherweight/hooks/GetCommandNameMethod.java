package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class GetCommandNameMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode getName = null;
	
	public boolean match(CustomClassNode node) {
		return node.equals(HelpCommandClass.baseCommandInterface);
	}
	
	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Type ret = Type.getReturnType(m.desc);
		return args.length == 0 && ret.equals(Type.getType(String.class));
	}
	
	public void reset() {
		super.reset();
		getName = null;
	}
	
	public void onComplete(CustomClassNode node, MethodNode m) {
		getName = m;
		if (FeatherWeight.debug) {
			System.out.println("Get Command Name Method: " + getName.name + getName.desc);
		}
	}
}
