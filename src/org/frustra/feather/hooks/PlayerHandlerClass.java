package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class PlayerHandlerClass extends MethodHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("multiplayer.player.joined");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		if (Type.getArgumentTypes(m.desc).length != 2) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.joined")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		Hooks.set("PlayerHandler", node);
		Hooks.set("PlayerHandler.playerJoined", m);
		Hooks.set("PlayerEntity", HookingHandler.getClassNode(args[1].getInternalName()));
	}
}
