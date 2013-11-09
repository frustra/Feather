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

public class PlayerHandlerClass extends InstructionHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("multiplayer.player.joined");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		return Type.getArgumentTypes(m.desc).length == 2;
	}
	
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.joined");
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Hooks.set("PlayerHandler", node);
		Hooks.set("PlayerHandler.playerJoined", m);
		Hooks.set("PlayerEntity", HookUtil.getClassNode(args[1].getInternalName()));
	}
}
