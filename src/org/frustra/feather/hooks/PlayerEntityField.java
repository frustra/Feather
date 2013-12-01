package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.FieldProvider;
import org.frustra.filament.hooking.types.HookingPass;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

@HookingPass(2)
public class PlayerEntityField extends FieldProvider {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.matches("PlayerSocketHandler");
	}

	public boolean match(FilamentClassNode node, FieldNode f) throws BadHookException {
		return HookUtil.compareType(Type.getType(f.desc), "PlayerEntity");
	}

	public void complete(FilamentClassNode node, FieldNode f) {
		Hooks.set("PlayerSocketHandler.playerEntity", f);
	}
}
