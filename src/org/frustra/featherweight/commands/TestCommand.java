package org.frustra.featherweight.commands;

import org.frustra.featherweight.Command;
import org.frustra.featherweight.Entity;

public class TestCommand extends Command {

	public String getName() {
		return "test";
	}

	public void execute(Entity source, String[] arguments) {
		System.out.println("test executed!");
		Command.respond(source, "foo", new Object[] { "bar", "baz" });

		for (String s : arguments) {
			System.out.println(s);
		}

		System.out.println("name: " + source.getName());
		Command.execute("kick " + source.getName());
	}

	public boolean hasPermission(Entity source) {
		return true;
	}
}
