package org.frustra.featherweight;

import java.io.File;
import java.lang.reflect.Method;

public class FeatherWeight {
	public static final String version = "1.0.0";
	
	public static void main(String[] args) throws Exception {
		System.out.println("FeatherWeight v" + FeatherWeight.version);
		
		File minecraftServer = new File("lib/minecraft_server.jar");
		if (!minecraftServer.exists()) {
			System.err.println("Minecraft server jar is missing");
			return;
		}
		
		CustomClassLoader loader = new CustomClassLoader(minecraftServer);
		
		loader.loadJar();
		
		//HookingHandler.loadHooks();
		//InjectionHandler.loadInjectors();

		Thread.currentThread().setContextClassLoader(loader);
		
		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] {String[].class});
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] {args});
	}
}
