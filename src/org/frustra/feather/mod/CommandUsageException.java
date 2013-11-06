package org.frustra.feather.mod;

import org.frustra.filament.injection.annotations.ReplaceSuperClass;

@ReplaceSuperClass("CommandUsageException")
public class CommandUsageException extends CommandExceptionProxy {
	@ReplaceSuperClass("CommandUsageException")
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
