package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class SendClientMessageMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode sendMessage = null;

	public boolean match(CustomClassNode node) {
		return node.equals(HelpCommandClass.baseCommand);
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Type ret = Type.getReturnType(m.desc);
		if ((m.access & Opcodes.ACC_STATIC) != 0 && ret.equals(Type.VOID_TYPE) && args.length == 3) {
			if (args[0].getClassName().equals(RconEntityClass.commandEntity.name) && args[1].equals(Type.getType(String.class)) && args[2].equals(Type.getType(Object[].class))) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		super.reset();
		sendMessage = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		sendMessage = m;
		if (FeatherWeight.debug) {
			System.out.println("Send Client Message Method: " + sendMessage.name + sendMessage.desc);
		}
	}
}
