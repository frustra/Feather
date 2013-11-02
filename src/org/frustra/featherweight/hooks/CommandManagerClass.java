package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class CommandManagerClass extends ClassHook implements HookingPassOne {
	public static CustomClassNode commandManager = null;
	public static String commandManagerInterface = null;

	public boolean match(CustomClassNode node) {
		return node.constants.contains("Couldn't process command");
	}

	public void reset() {
		super.reset();
		commandManager = null;
		commandManagerInterface = null;
	}

	public void onComplete(CustomClassNode node) {
		commandManager = node;
		commandManagerInterface = (String) commandManager.interfaces.get(0);
		if (FeatherWeight.debug) {
			System.out.println("Command Manager Class: " + commandManager.name);
			System.out.println("Command Manager Interface: " + commandManagerInterface);
		}
	}
}
