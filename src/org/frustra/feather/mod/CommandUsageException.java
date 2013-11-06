package org.frustra.feather.mod;

import org.frustra.filament.injection.annotations.ReplaceSuperClass;

@ReplaceSuperClass("CommandUsageExceptionClass.commandUsageException")
public class CommandUsageException extends CommandExceptionProxy {
	@ReplaceSuperClass("CommandUsageExceptionClass.commandUsageException")
	public CommandUsageException(String str, Object[] obj) {
		super(str, obj);
	}

	public CommandUsageException(String str) {
		this(str, new Object[0]);
	}

	public CommandUsageException(Command cmd) {
		this(cmd.getUsage(null), new Object[0]);
	}
}

class CommandUsageExceptionProxy extends RuntimeException {
	public CommandUsageExceptionProxy(String str, Object[] obj) {

	}
}
