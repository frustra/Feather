package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.FieldHook;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

public class MinecraftServerClass extends FieldHook implements HookingPassTwo {
	public static CustomClassNode minecraftServer = null;
	public static FieldNode commandManager = null;

	public boolean match(CustomClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(CustomClassNode node, FieldNode f) {
		return Type.getType(f.desc).getClassName().equals(CommandManagerClass.commandManagerInterface);
	}

	public void reset() {
		super.reset();
		minecraftServer = null;
		commandManager = null;
	}

	public void onComplete(CustomClassNode node, FieldNode f) {
		minecraftServer = node;
		commandManager = f;
		if (FeatherWeight.debug) {
			System.out.println("Command Manager Instance Field: " + commandManager.name);
		}
	}
}
