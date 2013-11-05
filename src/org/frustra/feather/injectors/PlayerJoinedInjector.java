package org.frustra.feather.injectors;

import java.util.List;

import org.frustra.feather.Feather;
import org.frustra.feather.hooks.PlayerConnectionHandlerClass;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PlayerJoinedInjector extends ClassInjector {
	public boolean match(CustomClassNode node) {
		return node.equals(PlayerConnectionHandlerClass.connectionHandler);
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (method.name.equals(PlayerConnectionHandlerClass.playerJoined.name) && method.desc.equals(PlayerConnectionHandlerClass.playerJoined.desc)) {
				InsnList iList = new InsnList();
				iList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Feather.class.getName().replace('.', '/'), "playerJoined", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
				method.instructions.insertBefore(method.instructions.getLast(), iList);
				break;
			}
		}
	}
}
