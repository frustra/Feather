package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.FieldHook;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.objectweb.asm.tree.FieldNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class PlayerEntityField extends FieldHook implements HookingPassTwo {
	public static FieldNode entity = null;

	public boolean match(CustomClassNode node) {
		return node.name.equals(PlayerSocketHandlerClass.socketHandler.name);
	}

	protected boolean match(CustomClassNode node, FieldNode f) {
		return Type.getObjectType(PlayerConnectionHandlerClass.playerEntity.name).getDescriptor().equals(f.desc);
	}

	public void reset() {
		super.reset();
		entity = null;
	}

	public void onComplete(CustomClassNode node, FieldNode f) {
		entity = f;
		if (Feather.debug) {
			System.out.println("Player Entity Field: " + entity.name);
		}
	}
}
