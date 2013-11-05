package org.frustra.feather.mod.logging;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.frustra.feather.Feather;

public class LogManager {
	public static Logger instance = null;

	public static Logger getLogger() {
		if (instance == null) {
			try {
				Class<?> managerClass = Feather.loader.loadClass("org.apache.logging.log4j.LogManager");
				Method m = managerClass.getDeclaredMethod("getLogger");

				instance = new Logger(m.invoke(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public static void syslog(Object param) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("[" + sdf.format(new Date()) + "] [Feather/INFO]: " + param.toString());
	}

	public static void syserr(Object param) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.err.println("[" + sdf.format(new Date()) + "] [Feather/ERROR]: " + param.toString());
	}

}
