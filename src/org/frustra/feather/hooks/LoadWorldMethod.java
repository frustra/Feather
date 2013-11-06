package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LoadWorldMethod extends MethodHook implements HookingPassOne {
	public static MethodNode loadWorld = null;

	public boolean match(CustomClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}
	
	public boolean match(CustomClassNode node, MethodNode m) {
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("menu.loadingLevel")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		loadWorld = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		loadWorld = m;
		if (Feather.debug) {
			System.out.println("Load World Method: " + loadWorld.name + loadWorld.desc);
		}
	}
}
