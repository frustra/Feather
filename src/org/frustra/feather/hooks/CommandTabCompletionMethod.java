package org.frustra.feather.hooks;

import java.util.List;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class CommandTabCompletionMethod extends InstructionHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.op.success");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = new Type[] { Type.getObjectType(Hooks.getClassName("CommandEntity")), Type.getType(String[].class) };
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(List.class), args));
	}

	private final String matchingDesc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] { Type.getType(String.class) });
	private Type lastReturnType = null;
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
			if (HookingHandler.compareType(lastReturnType, "PlayerHandler") && ((MethodInsnNode) insn).desc.equals(matchingDesc)) return true;
			lastReturnType = Type.getReturnType(((MethodInsnNode) insn).desc);
		}
		return false;
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("OpCommand", node);
		Hooks.set("Command.getCompletionList", m);
		Hooks.set("PlayerHandler.isOperator", new MethodNode(Opcodes.ACC_PUBLIC, ((MethodInsnNode) insn).name, ((MethodInsnNode) insn).desc, null, null));
	}
}
