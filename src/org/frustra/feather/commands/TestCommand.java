package org.frustra.feather.commands;

import org.frustra.feather.Command;
import org.frustra.feather.Entity;

public class TestCommand extends Command {

	public String getName() {
		return "test";
	}

	public void execute(Entity source, String[] arguments) {
		System.out.println("test executed!");
		source.respond("your message was received, %s!", new Object[] { source.getName() });

		for (String s : arguments) {
			System.out.println(s);
		}

		System.out.println("name: " + source.getName());
		//Command.execute("kick " + source.getName());
	}

	public boolean hasPermission(Entity source) {
		return true;
	}
}
