package org.frustra.feather.commands;

import org.frustra.feather.Command;
import org.frustra.feather.Entity;
import org.frustra.feather.LogManager;

public class TestCommand extends Command {

	public String getName() {
		return "test";
	}

	public void execute(Entity source, String[] arguments) {
		LogManager.getLogger().debug("test executed!");
		source.respond("Your message was received, %s!", new Object[] { source.getName() });
	}

	public boolean hasPermission(Entity source) {
		return true;
	}
}
