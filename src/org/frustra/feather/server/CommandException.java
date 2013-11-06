package org.frustra.feather.server;

import org.frustra.filament.injection.annotations.ReplaceSuperClass;

@ReplaceSuperClass("CommandException")
public class CommandException extends CommandExceptionProxy {
	@ReplaceSuperClass("CommandException")
	public CommandException(String str, Object[] obj) {
		super(str, obj);
	}

	public CommandException(String str) {
		this(str, new Object[0]);
	}
}

class CommandExceptionProxy extends RuntimeException {
	public CommandExceptionProxy(String str, Object[] obj) {

	}
}
