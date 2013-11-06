package org.frustra.feather.hooks;

import java.util.List;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class CommandTabCompletionMethod extends MethodHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.op.success");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = new Type[] { Type.getObjectType(Hooks.getClassName("CommandEntity")), Type.getType(String[].class) };
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(List.class), args));
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		Hooks.set("OpCommand", node);
		Hooks.set("Command.getCompletionList", m);
		AbstractInsnNode insn = m.instructions.getFirst();
		MethodInsnNode curr;
		Type lastReturnType = null;
		while (insn != null) {
			if (insn instanceof MethodInsnNode) {
				curr = (MethodInsnNode) insn;
				if (curr.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					String matchingDesc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] { Type.getType(String.class) });
					if (lastReturnType != null && HookingHandler.compareType(lastReturnType, "PlayerHandler") && curr.desc.equals(matchingDesc)) {
						Hooks.set("PlayerHandler.isOperator", new MethodNode(Opcodes.ACC_PUBLIC, curr.name, curr.desc, null, null));
						break;
					}
					lastReturnType = Type.getReturnType(curr.desc);
				}
			}
			insn = insn.getNext();
		}
	}
}
