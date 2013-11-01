package org.frustra.featherweight.hooks;

import org.frustra.featherweight.FeatherWeight;
import org.frustra.filament.FilamentStorage;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.ClassHook;
import org.frustra.filament.hooking.types.HookingPassOne;

public class RconEntityClass extends ClassHook implements HookingPassOne {
	public static CustomClassNode rconEntity = null;
	public static CustomClassNode commandEntity = null;
	
	public boolean match(CustomClassNode node) {
		return node.constants.contains("Rcon");
	}
	
	public void reset() {
		super.reset();
		rconEntity = null;
		commandEntity = null;
	}
	
	public void onComplete(CustomClassNode node) {
		rconEntity = node;
		commandEntity = FilamentStorage.store.classes.get((String) rconEntity.interfaces.get(0));
		if (FeatherWeight.debug) {
			System.out.println("Rcon Entity Class: " + rconEntity.name);
			System.out.println("Command Entity Interface: " + commandEntity.name);
		}
	}
}
