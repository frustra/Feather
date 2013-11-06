package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RconEntityClass extends MethodHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("Rcon");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		if (!m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("Rcon")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		Hooks.set("RconEntity", node);
		Hooks.set("CommandEntity", HookingHandler.getClassNode((String) node.interfaces.get(0)));
		Hooks.set("CommandEntity.getName", m);
	}
}
