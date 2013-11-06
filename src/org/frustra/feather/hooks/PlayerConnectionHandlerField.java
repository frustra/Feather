package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.FieldHook;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

public class PlayerConnectionHandlerField extends FieldHook implements HookingPassTwo {
	public static CustomClassNode minecraftServer = null;
	public static FieldNode playerConnectionHandler = null;

	public boolean match(CustomClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(CustomClassNode node, FieldNode f) {
		return Type.getType(f.desc).getClassName().equals(PlayerConnectionHandlerClass.connectionHandler.name);
	}

	public void reset() {
		super.reset();
		minecraftServer = null;
		playerConnectionHandler = null;
	}

	public void onComplete(CustomClassNode node, FieldNode f) {
		minecraftServer = node;
		playerConnectionHandler = f;
		if (Feather.debug) {
			System.out.println("Player Connection Handler Instance Field: " + playerConnectionHandler.name);
		}
	}
}
