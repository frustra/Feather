package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.FilamentStorage;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class HelpCommandClass extends ClassHook implements HookingPassOne {
	public static CustomClassNode helpCommand = null;
	public static CustomClassNode baseCommand = null;
	public static CustomClassNode baseCommandInterface = null;
	
	public boolean match(CustomClassNode node) {
		return node.constants.contains("commands.help.usage");
	}
	
	public void reset() {
		super.reset();
		helpCommand = null;
		baseCommand = null;
		baseCommandInterface = null;
	}
	
	public void onComplete(CustomClassNode node) {
		helpCommand = node;
		baseCommand = FilamentStorage.store.classes.get(node.superName.replace('/', '.'));
		baseCommandInterface = FilamentStorage.store.classes.get(((String) baseCommand.interfaces.get(0)).replace('/', '.'));
		if (FeatherWeight.debug) {
			System.out.println("Help Command Class: " + helpCommand.name);
			System.out.println("Base Command Class: " + baseCommand.name);
			System.out.println("Base Command Interface: " + baseCommandInterface.name);
		}
	}
}
