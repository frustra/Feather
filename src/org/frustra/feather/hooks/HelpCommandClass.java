package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
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
		baseCommand = HookingHandler.getClassNode(node.superName);
		baseCommandInterface = HookingHandler.getClassNode((String) baseCommand.interfaces.get(0));
		if (Feather.debug) {
			System.out.println("Help Command Class: " + helpCommand.name);
			System.out.println("Base Command Class: " + baseCommand.name);
			System.out.println("Base Command Interface: " + baseCommandInterface.name);
		}
	}
}
