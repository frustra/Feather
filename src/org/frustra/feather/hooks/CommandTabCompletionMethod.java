package org.frustra.feather.hooks;

import java.util.List;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.InstructionProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(2)
public class CommandTabCompletionMethod extends InstructionProvider {
	public boolean match(FilamentClassNode node) {
		return node.containsConstant("commands.op.success");
	}

	public boolean match(FilamentClassNode node, MethodNode m) throws BadHookException {
		Type[] args = new Type[] { Type.getObjectType(Hooks.getClassName("CommandEntity")), Type.getType(String[].class) };
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(List.class), args));
	}

	private final String matchingDesc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] { Type.getType(String.class) });
	private Type lastReturnType = null;

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) throws BadHookException {
		if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
			if (HookUtil.compareType(lastReturnType, "PlayerHandler") && ((MethodInsnNode) insn).desc.equals(matchingDesc)) return true;
			lastReturnType = Type.getReturnType(((MethodInsnNode) insn).desc);
		}
		return false;
	}

	public void complete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("OpCommand", node);
		Hooks.set("Command.getCompletionList", m);
		Hooks.set("PlayerHandler.isOperator", new MethodNode(Opcodes.ACC_PUBLIC, ((MethodInsnNode) insn).name, ((MethodInsnNode) insn).desc, null, null));
	}
}
