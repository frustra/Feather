package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class PlayerHandlerClass extends MethodHook implements HookingPassOne {
	public static CustomClassNode playerHandler = null;
	public static MethodNode playerJoined = null;
	public static CustomClassNode playerEntity = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("multiplayer.player.joined");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		if (args.length != 2) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("multiplayer.player.joined")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		playerHandler = null;
		playerJoined = null;
		playerEntity = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		playerHandler = node;
		playerJoined = m;
		Type[] args = Type.getArgumentTypes(m.desc);
		playerEntity = HookingHandler.getClassNode(args[1].getInternalName());
		if (Feather.debug) {
			System.out.println("Player Handler Class: " + playerHandler.name);
			System.out.println("Player Joined Method: " + playerJoined.name + playerJoined.desc);
			System.out.println("Player Entity Class: " + playerEntity.name);
		}
	}
}
