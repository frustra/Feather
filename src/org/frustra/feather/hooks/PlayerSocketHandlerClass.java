package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PlayerSocketHandlerClass extends MethodHook implements HookingPassOne {
	public static CustomClassNode socketHandler = null;
	public static MethodNode playerLeft = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("multiplayer.player.left");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.left")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		socketHandler = null;
		playerLeft = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		socketHandler = node;
		playerLeft = m;
		if (Feather.debug) {
			System.out.println("Player Socket Handler Class: " + socketHandler.name);
			System.out.println("Player Left Method: " + playerLeft.name + playerLeft.desc);
		}
	}
}
