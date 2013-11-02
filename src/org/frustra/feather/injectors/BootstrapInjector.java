package org.frustra.feather.injectors;

import java.util.List;

import org.frustra.feather.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BootstrapInjector extends ClassInjector {
	public boolean match(CustomClassNode node) {
		return node.name.equals("net/minecraft/server/MinecraftServer");
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (method.name.equals("<init>")) {
				InsnList iList = new InsnList();
				iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FeatherWeight.class.getName().replace('.', '/'), "bootstrap", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
				method.instructions.insertBefore(method.instructions.getLast(), iList);
				break;
			}
		}
	}
}
