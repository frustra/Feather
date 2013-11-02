package org.frustra.feather.hooks;

import org.frustra.feather.Feather;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.types.HookingPassTwo;
import org.frustra.filament.hooking.types.MethodHook;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class AddCommandMethod extends MethodHook implements HookingPassTwo {
	public static MethodNode addCommand = null;

	public boolean match(CustomClassNode node) {
		return node.equals(CommandManagerClass.commandManager);
	}

	public boolean match(CustomClassNode node, MethodNode m) {
		Type[] args = Type.getArgumentTypes(m.desc);
		return args.length == 1 && args[0].getInternalName().equals(HelpCommandClass.baseCommandInterface.name);
	}

	public void reset() {
		super.reset();
		addCommand = null;
	}

	public void onComplete(CustomClassNode node, MethodNode m) {
		addCommand = m;
		if (Feather.debug) {
			System.out.println("Add Command Method: " + addCommand.name + addCommand.desc);
		}
	}
}
