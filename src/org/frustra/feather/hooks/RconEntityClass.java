package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookUtil;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RconEntityClass extends InstructionHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("Rcon");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]));
	}
	
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("Rcon");
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("RconEntity", node);
		Hooks.set("CommandEntity", HookUtil.getClassNode((String) node.interfaces.get(0)));
		Hooks.set("CommandEntity.getName", m);
	}
}
