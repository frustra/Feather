package org.frustra.feather.mod.logging;

import java.lang.reflect.Method;

import org.frustra.feather.Feather;

public class Logger {
	private Object baseLogger;

	private static Method _debug, _error, _fatal, _info, _trace, _warn;

	public Logger(Object baseLogger) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		this.baseLogger = baseLogger;

		Class<?> loggerClass = Feather.loader.loadClass("org.apache.logging.log4j.Logger");
		_debug = loggerClass.getDeclaredMethod("debug", new Class[] { Object.class });
		_error = loggerClass.getDeclaredMethod("error", new Class[] { Object.class });
		_fatal = loggerClass.getDeclaredMethod("fatal", new Class[] { Object.class });
		_trace = loggerClass.getDeclaredMethod("trace", new Class[] { Object.class });
		_info = loggerClass.getDeclaredMethod("info", new Class[] { Object.class });
		_warn = loggerClass.getDeclaredMethod("warn", new Class[] { Object.class });
	}

	public void debug(Object param) {
		try {
			_debug.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void error(Object param) {
		try {
			_error.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fatal(Object param) {
		try {
			_fatal.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void info(Object param) {
		try {
			_info.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void trace(Object param) {
		try {
			_trace.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void warn(Object param) {
		try {
			_warn.invoke(baseLogger, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
