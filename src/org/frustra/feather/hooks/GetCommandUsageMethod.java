package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GetCommandUsageMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode getUsage = null;

	public boolean match(CustomClassNode node) {
		return node.equals(HelpCommandClass.helpCommand);
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type ret = Type.getReturnType(m.desc);
		if (!ret.equals(Type.getType(String.class))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("commands.help.usage")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		getUsage = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		getUsage = m;
		if (Feather.debug) {
			System.out.println("Get Command Usage Method: " + getUsage.name + getUsage.desc);
		}
	}
}
