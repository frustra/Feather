package org.frustra.feather.injectors;

import java.util.List;

import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookUtil;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SpawnProtectionInjector extends ClassInjector {
	public boolean match(CustomClassNode node) throws BadHookException {
		return node.matches("ServerProperties");
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) throws BadHookException {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (HookUtil.compareMethodNode(method, "ServerProperties.isSpawnProtected")) {
				AbstractInsnNode insn = method.instructions.getFirst();
				AbstractInsnNode start = null;
				while (insn != null) {
					if (insn.getOpcode() == Opcodes.INVOKEINTERFACE && ((MethodInsnNode) insn).name.equals("isEmpty")) {
						while (start != null && start.getOpcode() != Opcodes.IRETURN) {
							insn = start;
							start = start.getNext();
							method.instructions.remove(insn);
						}
						if (start != null) method.instructions.remove(start);
						return;
					} else if (insn instanceof LabelNode) {
						start = insn.getNext();
					}
					insn = insn.getNext();
				}
			}
		}
	}
}
