package org.frustra.feather.hooks;

import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.hooking.Hooks;
import org.frustra.filament.hooking.types.FieldHook;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

public class MinecraftServerClass extends FieldHook implements HookingPassTwo {
	public boolean match(CustomClassNode node) {
		return node.name.endsWith("MinecraftServer");
	}

	public boolean match(CustomClassNode node, FieldNode f) {
		return HookingHandler.compareType(Type.getType(f.desc), "BaseCommandManager");
	}

	public void onComplete(CustomClassNode node, FieldNode f) {
		Hooks.set("MinecraftServer", node);
		Hooks.set("MinecraftServer.commandManager", f);
	}
}
