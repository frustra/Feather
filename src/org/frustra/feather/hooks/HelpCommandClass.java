package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class HelpCommandClass extends MethodHook implements HookingPassOne {
	public static CustomClassNode helpCommand = null;
	public static CustomClassNode baseCommand = null;
	public static CustomClassNode baseCommandInterface = null;
	public static MethodNode getCommandName = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.help.usage");
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		if (!m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("help")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		helpCommand = null;
		baseCommand = null;
		baseCommandInterface = null;
		getCommandName = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		helpCommand = node;
		baseCommand = HookingHandler.getClassNode(node.superName);
		baseCommandInterface = HookingHandler.getClassNode((String) baseCommand.interfaces.get(0));
		getCommandName = m;
		if (Feather.debug) {
			System.out.println("Help Command Class: " + helpCommand.name);
			System.out.println("Base Command Class: " + baseCommand.name);
			System.out.println("Base Command Interface: " + baseCommandInterface.name);
			System.out.println("Get Command Name Method: " + getCommandName.name + getCommandName.desc);
		}
	}
}
