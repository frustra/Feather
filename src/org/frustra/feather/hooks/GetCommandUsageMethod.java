package org.frustra.feather.hooks;

import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GetCommandUsageMethod extends InstructionHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) throws BadHookException {
		return node.matches("HelpCommand");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		return Type.getReturnType(m.desc).equals(Type.getType(String.class));
	}
	
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("commands.help.usage");
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("Command.getUsage", m);
	}
}
