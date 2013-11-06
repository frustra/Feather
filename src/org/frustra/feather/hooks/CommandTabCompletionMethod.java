package org.frustra.feather.hooks;

import java.util.List;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class CommandTabCompletionMethod extends MethodHook implements HookingPassTwo {
	public static CustomClassNode opCommand = null;
	public static MethodNode tabCompletion = null;
	public static MethodNode isOperator = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.op.success");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] arguments = new Type[] { Type.getObjectType(RconEntityClass.commandEntity.name), Type.getType(String[].class) };
		if (!m.desc.equals(Type.getMethodDescriptor(Type.getType(List.class), arguments))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		MethodInsnNode curr;
		Type lastReturnType = null;
		while (insn != null) {
			if (insn instanceof MethodInsnNode) {
				curr = (MethodInsnNode) insn;
				if (curr.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					String matchingDesc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] { Type.getType(String.class) });
					if (lastReturnType != null && lastReturnType.getInternalName().equals(PlayerConnectionHandlerClass.connectionHandler.name) && curr.desc.equals(matchingDesc)) {
						isOperator = new MethodNode(Opcodes.ACC_PUBLIC, curr.name, curr.desc, null, null);
						break;
					}
					lastReturnType = Type.getReturnType(curr.desc);
				}
			}
			insn = insn.getNext();
		}
		return true;
	}

	public void reset() {
		super.reset();
		opCommand = null;
		tabCompletion = null;
		isOperator = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		opCommand = node;
		tabCompletion = m;
		if (Feather.debug) {
			System.out.println("Op Command Class: " + opCommand.name);
			System.out.println("Tab Completion Method: " + tabCompletion.name + tabCompletion.desc);
			System.out.println("Is Operator Method: " + isOperator.name + isOperator.desc);
		}
	}
}
