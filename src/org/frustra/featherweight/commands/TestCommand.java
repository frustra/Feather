package org.frustra.featherweight.commands;

public class TestCommand extends Command {

	@Override
	public String getName() {
		return "test";
	}

	@Override
	public boolean execute(Object state, String[] arguments) {
		System.out.println("test executed!");
		for (String s : arguments) {
			System.out.println(s);
		}
		return false;
	}

}
