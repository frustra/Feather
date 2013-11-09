package org.frustra.feather;

import java.io.File;
import java.lang.reflect.Method;

import org.frustra.feather.server.logging.LogManager;
import org.frustra.filament.FilamentClassLoader;
import org.frustra.filament.hooking.Hooking;
import org.frustra.filament.injection.Injection;

public class Feather {
	public static final String version = "1.0.0";
	public static final boolean debug = true;

	public static FilamentClassLoader loader = null;

	public static final String[] includePackages = new String[] {
		"org.frustra.feather.server",
		"org.frustra.feather.server.commands",
		"org.frustra.feather.server.logging",
		"org.frustra.feather.server.voting"
	};

	public static void main(String[] args) throws Exception {
		LogManager.syslog("Feather version " + Feather.version + " starting up");
		if (Feather.debug) LogManager.syslog("Debug logging is enabled");

		File minecraftServer = new File("lib/minecraft_server.jar");
		if (!minecraftServer.exists()) {
			LogManager.syserr("Minecraft server jar is missing");
			return;
		}

		loader = new FilamentClassLoader(debug) {
			protected Class<?> defineClass(String name, byte[] buf) {
				return defineClass(name, buf, 0, buf.length);
			}
		};
		
		loader.loadJar(minecraftServer);
		Thread.currentThread().setContextClassLoader(loader);

		for (String packageName : includePackages) {
			loader.loadPackage(packageName);
		}

		Hooking.loadHooks("org.frustra.feather.hooks");
		Injection.loadInjectors("org.frustra.feather.injectors");

		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] { String[].class });
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] { args });
	}
}
