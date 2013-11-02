package org.frustra.featherweight;

import java.util.List;

import org.frustra.featherweight.hooks.RconEntityClass;
import org.frustra.filament.hooking.CustomClassNode;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Injection {

	@SuppressWarnings("unchecked")
	public static void injectNode(CustomClassNode node, CustomClassLoader loader) {
		if (node.name.equals("net/minecraft/server/MinecraftServer")) {
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("<init>")) {
					InsnList iList = new InsnList();
					iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FeatherWeight.class.getName().replace('.', '/'), "bootstrap", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
					method.instructions.insertBefore(method.instructions.getLast(), iList);
					break;
				}
			}
		} else if (node.name.equals(Entity.class.getName().replace('.', '/'))) {
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("getName")) {
					method.instructions.clear();
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, "entity", Type.getDescriptor(Object.class)));
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, RconEntityClass.commandEntity.name, "b_", Type.getMethodDescriptor(Type.getType(String.class), new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.ARETURN));
				}
			}
		}
	}
}
