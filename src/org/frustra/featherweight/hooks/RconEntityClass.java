package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.FilamentStorage;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassOne;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class RconEntityClass extends MethodHook implements HookingPassOne {
	public static CustomClassNode rconEntity = null;
	public static CustomClassNode commandEntity = null;
	public static MethodNode getEntityName = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("Rcon");
	}

	protected boolean match(CustomClassNode node, MethodNode m) {
		if (!m.desc.equals(Type.getMethodDescriptor(Type.getType(String.class), new Type[0]))) return false;
		AbstractInsnNode insn = m.instructions.getFirst();
		while (insn != null) {
			if (insn instanceof LdcInsnNode) {
				if (((LdcInsnNode) insn).cst.toString().equals("Rcon")) return true;
			}
			insn = insn.getNext();
		}
		return false;
	}

	public void reset() {
		super.reset();
		rconEntity = null;
		commandEntity = null;
		getEntityName = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		rconEntity = node;
		commandEntity = FilamentStorage.store.classes.get((String) rconEntity.interfaces.get(0));
		getEntityName = m;
		if (FeatherWeight.debug) {
			System.out.println("Rcon Entity Class: " + rconEntity.name);
			System.out.println("Command Entity Interface: " + commandEntity.name);
			System.out.println("Get Entity Name Method: " + getEntityName.name + getEntityName.desc);
		}
	}
}
