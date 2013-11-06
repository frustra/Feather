package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

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
		Hooks.set("PlayerEntity", HookingHandler.getClassNode(args[1].getInternalName()));
	}
}
