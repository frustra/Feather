package org.frustra.feather.mod.commands;

import org.frustra.feather.mod.logging.LogManager;
import org.frustra.feather.mod.server.Entity;

public class TestCommand extends Command {
	public String getName() {
		return "test";
	}

	public void execute(Entity source, String[] arguments) {
		LogManager.getLogger().info("test executed!");
		source.sendMessage("Your message was received, %s!", new Object[] { source.getName() });
	}

	public boolean hasPermission(Entity source) {
		return true;
	}

	public String getUsage(Entity source) {
		return "/test";
	}
}
