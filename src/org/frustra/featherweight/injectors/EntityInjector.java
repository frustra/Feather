package org.frustra.featherweight.injectors;

import java.util.List;

import org.frustra.featherweight.Entity;
import org.frustra.featherweight.hooks.RconEntityClass;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class EntityInjector extends ClassInjector {
	public boolean match(CustomClassNode node) {
		return node.name.equals(Entity.class.getName().replace('.', '/'));
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (method.name.equals("getName")) {
				method.instructions.clear();
				method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, "entity", Type.getDescriptor(Object.class)));
				method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, RconEntityClass.commandEntity.name, RconEntityClass.getEntityName.name, Type.getMethodDescriptor(Type.getType(String.class), new Type[0])));
				method.instructions.add(new InsnNode(Opcodes.ARETURN));
			}
		}
	}
}
