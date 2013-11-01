package org.frustra.featherweight.commands;

public class TestCommand extends Command {

	public String getName() {
		return "test";
	}

	public void execute(Object state, String[] arguments) {
		System.out.println("test executed!");
		for (String s : arguments) {
			System.out.println(s);
		}
	}
	
	public boolean hasPermission(Object state) {
		return true;
	}
}
