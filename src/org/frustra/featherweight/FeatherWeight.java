package org.frustra.featherweight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.frustra.featherweight.commands.TestCommand;
import org.frustra.featherweight.commands.VoteKickCommand;
import org.frustra.featherweight.hooks.AddCommandMethod;
import org.frustra.featherweight.hooks.CommandManagerClass;
import org.frustra.featherweight.hooks.ExecuteCommandMethod;
import org.frustra.featherweight.hooks.GetCommandNameMethod;
import org.frustra.featherweight.hooks.HandleExecuteCommandMethod;
import org.frustra.featherweight.hooks.HasCommandPermissionMethod;
import org.frustra.featherweight.hooks.HelpCommandClass;
import org.frustra.featherweight.hooks.MinecraftServerClass;
import org.frustra.featherweight.hooks.RconEntityClass;
import org.frustra.featherweight.hooks.SendClientMessageMethod;
import org.frustra.filament.FilamentStorage;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.injection.InjectionHandler;
import org.objectweb.asm.ClassReader;

public class FeatherWeight {
	public static final String version = "1.0.0";
	public static final boolean debug = true;
	
	public static Object minecraftServer = null;
	public static Object commandManager = null;
	public static CustomClassLoader loader = null;
	
	public static final Class<?>[] hooks = new Class<?>[] {
		AddCommandMethod.class,
		CommandManagerClass.class,
		ExecuteCommandMethod.class,
		GetCommandNameMethod.class,
		HandleExecuteCommandMethod.class,
		HasCommandPermissionMethod.class,
		HelpCommandClass.class,
		MinecraftServerClass.class,
		RconEntityClass.class,
		SendClientMessageMethod.class
	};
	
	public static final Class<?>[] injectors = new Class<?>[] {
			
	};
	
	public static final Class<?>[] mods = new Class<?>[] {
		TestCommand.class,
		VoteKickCommand.class,
		Entity.class
	};

	public static void main(String[] args) throws Exception {
		System.out.println("FeatherWeight v" + FeatherWeight.version);
		if (FeatherWeight.debug) System.out.println("Debug logging is enabled");

		File minecraftServer = new File("lib/minecraft_server.jar");
		if (!minecraftServer.exists()) {
			System.err.println("Minecraft server jar is missing");
			return;
		}

		CustomClassLoader loader = new CustomClassLoader(minecraftServer);
		Thread.currentThread().setContextClassLoader(loader);

		HookingHandler.loadJar(loader.store.jarFile);
		for (Class<?> cls : mods) {
			loader.store.filament.classes.put(cls.getName(), loadOwnClass(cls.getName()));
		}
		
		HookingHandler.loadHooks(hooks);
		InjectionHandler.loadInjectors(injectors);

		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] { String[].class });
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] { args });
	}

	public static CustomClassNode loadOwnClass(String name) throws IOException {
		InputStream classStream = FeatherWeight.class.getResourceAsStream("/" + name.replace('.', '/') + ".class");
		CustomClassNode node = new CustomClassNode();
		ClassReader reader = new ClassReader(classStream);
		reader.accept(node, 0);
		return node;
	}

	public static void bootstrap(Object minecraftServer) throws Exception {
		FeatherWeight.loader = (CustomClassLoader) minecraftServer.getClass().getClassLoader();
		if (FilamentStorage.store == null) {
			System.out.println("Set storage");
			FilamentStorage.store = FeatherWeight.loader.store.filament;
		}
		HookingHandler.doHooking();
		
		FeatherWeight.minecraftServer = minecraftServer;
		Injection.injectServer(minecraftServer);
	}
}
