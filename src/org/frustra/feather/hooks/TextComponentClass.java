package org.frustra.feather.hooks;

import org.frustra.filament.HookUtil;
import org.frustra.filament.Hooks;
import org.frustra.filament.hooking.FilamentClassNode;
import org.frustra.filament.hooking.types.ClassProvider;
import org.frustra.filament.hooking.types.HookingPass;

@HookingPass(1)
public class TextComponentClass extends ClassProvider {
	public boolean match(FilamentClassNode node) {
		return node.containsConstant("TextComponent{text='");
	}

	public void complete(FilamentClassNode node) {
		Hooks.set("TextComponent", node);
		FilamentClassNode baseComponent = HookUtil.getClassNode(node.superName);
		Hooks.set("BaseComponent", baseComponent);
		Hooks.set("FormatableComponent", HookUtil.getClassNode((String) baseComponent.interfaces.get(0)));
	}
}
