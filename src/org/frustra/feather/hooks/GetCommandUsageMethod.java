package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GetCommandUsageMethod extends MethodHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.equals(Hooks.getClass("HelpCommand"));
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		if (!Type.getReturnType(m.desc).equals(Type.getType(String.class))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("commands.help.usage")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		Hooks.set("Command.getUsage", m);
	}
}
