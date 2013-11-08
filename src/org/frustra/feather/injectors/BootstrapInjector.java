package org.frustra.feather.injectors;

import java.util.List;

import org.frustra.feather.server.Bootstrap;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookUtil;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class BootstrapInjector extends ClassInjector {
	public boolean match(CustomClassNode node) {
		return node.name.equals("net/minecraft/server/MinecraftServer");
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) throws BadHookException {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (method.name.equals("<init>")) {
				InsnList iList = new InsnList();
				iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				iList.add(HookUtil.createMethodInsnNode(Opcodes.INVOKESTATIC, Bootstrap.class, "bootstrap", void.class, Object.class));
				method.instructions.insertBefore(method.instructions.getLast(), iList);
			} else if (HookUtil.compareMethodNode(method, "MinecraftServer.loadWorld")) {
				InsnList iList = new InsnList();
				iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				iList.add(HookUtil.createMethodInsnNode(Opcodes.INVOKESTATIC, Bootstrap.class, "worldLoaded", void.class));
				method.instructions.insertBefore(method.instructions.getLast(), iList);
			}
		}
	}
}
