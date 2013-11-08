package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookUtil;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.InstructionHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class HelpCommandClass extends InstructionHook implements HookingPassOne {
	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.help.usage");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		return m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]));
	}
	
	public boolean match(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		return insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst.toString().equals("help");
	}

	public void onComplete(CustomClassNode node, MethodNode m, AbstractInsnNode insn) {
		CustomClassNode commandClass = HookUtil.getClassNode(node.superName);
		Hooks.set("HelpCommand", node);
		Hooks.set("Command", commandClass);
		Hooks.set("BaseCommand", HookUtil.getClassNode((String) commandClass.interfaces.get(0)));
		Hooks.set("Command.getName", m);
	}
}
