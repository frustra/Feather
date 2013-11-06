package org.frustra.feather.server.commands;

import org.frustra.feather.server.Command;
import org.frustra.feather.server.Entity;
import org.frustra.feather.server.logging.LogManager;

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
