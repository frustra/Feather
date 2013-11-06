package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.FieldHook;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

public class PlayerHandlerField extends FieldHook implements HookingPassTwo {
	public static FieldNode playerHandler = null;

	public boolean match(CustomClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(CustomClassNode node, FieldNode f) {
		return Type.getType(f.desc).getClassName().equals(PlayerHandlerClass.playerHandler.name);
	}

	public void reset() {
		super.reset();
		playerHandler = null;
	}

	public void onComplete(CustomClassNode node, FieldNode f) {
		playerHandler = f;
		if (Feather.debug) {
			System.out.println("Player Handler Instance Field: " + playerHandler.name);
		}
	}
}
