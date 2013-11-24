package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LoadWorldMethod extends InstructionHook implements HookingPassOne {
	public boolean match(FilamentClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("menu.loadingLevel");
	}

	public void onComplete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("MinecraftServer.loadWorld", m);
	}
}
