package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.InstructionProvider;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(2)
public class GetCommandUsageMethod extends InstructionProvider {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.matches("HelpCommand");
	}

	public boolean match(FilamentClassNode node, MethodNode m) {
		return Type.getReturnType(m.desc).equals(Type.getType(String.class));
	}

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("commands.help.usage");
	}

	public void complete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		Hooks.set("Command.getUsage", m);
	}
}
