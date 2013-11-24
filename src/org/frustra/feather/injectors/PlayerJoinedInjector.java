package org.frustra.feather.injectors;

import java.util.List;

import org.frustra.feather.server.Bootstrap;
import org.frustra.filament.HookUtil;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PlayerJoinedInjector extends ClassInjector {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.matches("PlayerHandler");
	}

	@SuppressWarnings("unchecked")
	public void inject(FilamentClassNode node) throws BadHookException {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (HookUtil.compareMethodNode(method, "PlayerHandler.playerJoined")) {
				InsnList iList = new InsnList();
				iList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				iList.add(HookUtil.createMethodInsnNode(Opcodes.INVOKESTATIC, Bootstrap.class, "playerJoined", void.class, Object.class));
				method.instructions.insertBefore(method.instructions.getLast(), iList);
				break;
			}
		}
	}
}
