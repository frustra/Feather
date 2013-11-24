package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class HelpCommandClass extends InstructionHook implements HookingPassOne {
	public boolean match(FilamentClassNode node) {
		return node.constants.contains("commands.help.usage");
	}

	public boolean match(FilamentClassNode node, MethodNode m) {
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]));
	}

	public boolean match(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("help");
	}

	public void onComplete(FilamentClassNode node, MethodNode m, AbstractInsnNode insn) {
		FilamentClassNode commandClass = HookUtil.getClassNode(node.superName);
		Hooks.set("HelpCommand", node);
		Hooks.set("Command", commandClass);
		Hooks.set("BaseCommand", HookUtil.getClassNode((String) commandClass.interfaces.get(0)));
		Hooks.set("Command.getName", m);
	}
}
