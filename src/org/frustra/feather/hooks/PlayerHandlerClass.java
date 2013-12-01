package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.InstructionProvider;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(1)
public class PlayerHandlerClass extends InstructionProvider {
	public boolean match(FilamentClassNode node) {
		return node.containsConstant("multiplayer.player.joined");
	}

	public boolean match(FilamentClassNode node, MethodNode m) {
		return Type.getArgumentTypes(m.desc).length == 2;
	}

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.joined");
	}

	public void complete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Hooks.set("PlayerHandler", node);
		Hooks.set("PlayerHandler.playerJoined", m);
		Hooks.set("PlayerEntity", HookUtil.getClassNode(args[1].getInternalName()));
	}
}
