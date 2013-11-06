package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PlayerSocketHandlerClass extends InstructionHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("multiplayer.player.left");
	}
	
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.left");
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("PlayerSocketHandler", node);
		Hooks.set("PlayerSocketHandler.playerLeft", m);
	}
}
