package org.frustra.feather.hooks;

import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.BadHookException;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.HookingPass;
import org.frustra.filament.hooking.types.MethodProvider;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

@HookingPass(2)
public class HasCommandPermissionMethod extends MethodProvider {
	public boolean match(FilamentClassNode node) throws BadHookException {
		return node.matches("BaseCommand");
	}

	public boolean match(FilamentClassNode node, MethodNode m) throws BadHookException {
		Type[] args = new Type[] { Type.getObjectType(Hooks.getClassName("CommandEntity")) };
		return m.desc.equals(Type.getMethodDescriptor(Type.BOOLEAN_TYPE, args));
	}

	public void complete(FilamentClassNode node, MethodNode m) {
		Hooks.set("Command.hasPermission", m);
	}
}
