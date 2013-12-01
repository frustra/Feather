package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.InstructionProvider;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(1)
public class LoadWorldMethod extends InstructionProvider {
	public boolean match(FilamentClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("menu.loadingLevel");
	}

	public void complete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("MinecraftServer.loadWorld", m);
	}
}
