package org.frustra.feather.mod;

import org.frustra.filament.injection.annotations.ReplaceSuperClass;

@ReplaceSuperClass("CommandExceptionClass.commandException")
public class CommandException extends CommandExceptionProxy {
	@ReplaceSuperClass("CommandExceptionClass.commandException")
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
