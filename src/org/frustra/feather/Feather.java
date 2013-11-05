package org.frustra.feather;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.frustra.feather.hooks.AddCommandMethod;
import org.frustra.feather.hooks.CommandManagerClass;
import org.frustra.feather.hooks.ExecuteCommandMethod;
import org.frustra.feather.hooks.GetCommandNameMethod;
import org.frustra.feather.hooks.HandleExecuteCommandMethod;
import org.frustra.feather.hooks.HasCommandPermissionMethod;
import org.frustra.feather.hooks.HelpCommandClass;
import org.frustra.feather.hooks.MinecraftServerClass;
import org.frustra.feather.hooks.PlayerConnectionHandlerClass;
import org.frustra.feather.hooks.PlayerEntityField;
import org.frustra.feather.hooks.PlayerSocketHandlerClass;
import org.frustra.feather.hooks.RconEntityClass;
import org.frustra.feather.hooks.SendClientMessageMethod;
import org.frustra.feather.injectors.BootstrapInjector;
import org.frustra.feather.injectors.PlayerJoinedInjector;
import org.frustra.feather.injectors.PlayerLeftInjector;
import org.frustra.feather.mod.commands.Command;
import org.frustra.feather.mod.commands.TestCommand;
import org.frustra.feather.mod.commands.VoteKickCommand;
import org.frustra.feather.mod.logging.LogManager;
import org.frustra.feather.mod.server.Entity;
import org.frustra.feather.mod.server.Player;
import org.frustra.feather.mod.server.PlayerListener;
import org.frustra.feather.mod.server.Server;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.hooking.HookingHandler;
import org.frustra.filament.injection.InjectionHandler;
import org.objectweb.asm.ClassReader;

public class Feather {
	public static final String version = "1.0.0";
	public static final boolean debug = true;

	public static Object minecraftServer = null;
	public static Object commandManager = null;
	public static CustomClassLoader loader = null;
	public static Server server = null;

	public static final Class<?>[] hooks = new Class<?>[] {
		AddCommandMethod.class,
		CommandManagerClass.class,
		ExecuteCommandMethod.class,
		GetCommandNameMethod.class,
		HandleExecuteCommandMethod.class,
		HasCommandPermissionMethod.class,
		HelpCommandClass.class,
		MinecraftServerClass.class,
		PlayerConnectionHandlerClass.class,
		PlayerEntityField.class,
		PlayerSocketHandlerClass.class,
		RconEntityClass.class,
		SendClientMessageMethod.class
	};

	public static final Class<?>[] injectors = new Class<?>[] {
		BootstrapInjector.class,
		PlayerJoinedInjector.class,
		PlayerLeftInjector.class
	};

	public static final Class<?>[] commands = new Class<?>[] {
		TestCommand.class,
		VoteKickCommand.class
	};

	public static void main(String[] args) throws Exception {
		LogManager.syslog("Feather version " + Feather.version + " starting up");
		if (Feather.debug) LogManager.syslog("Debug logging is enabled");

		File minecraftServer = new File("lib/minecraft_server.jar");
		if (!minecraftServer.exists()) {
			LogManager.syserr("Minecraft server jar is missing");
			return;
		}

		loader = new CustomClassLoader(minecraftServer);
		Thread.currentThread().setContextClassLoader(loader);

		HookingHandler.loadJar(loader.jarFile);
		loadOwnClass(Command.class.getName());

		for (Class<?> command : commands) {
			loadOwnClass(command.getName());
		}

		HookingHandler.loadHooks(hooks);
		InjectionHandler.loadInjectors(injectors);

		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] { String[].class });
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] { args });
	}

	public static void loadOwnClass(String name) throws IOException {
		InputStream classStream = Feather.class.getResourceAsStream("/" + name.replace('.', '/') + ".class");
		CustomClassNode node = new CustomClassNode();
		ClassReader reader = new ClassReader(classStream);
		reader.accept(node, 0);
		loader.filament.classes.put(name, node);
	}

	public static void bootstrap(Object minecraftServer) throws Exception {
		loader = (CustomClassLoader) minecraftServer.getClass().getClassLoader();
		Thread.currentThread().setName("Feather");

		Feather.server = new Server();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				this.setName("Feather");
				LogManager.getLogger().info("Feather shutting down");
				Feather.server.shutdown();
			}
		});

		Field commandManagerField = HookingHandler.lookupField(MinecraftServerClass.minecraftServer, MinecraftServerClass.commandManager);
		Feather.commandManager = commandManagerField.get(minecraftServer);
		Feather.minecraftServer = minecraftServer;

		Method addCommandMethod = HookingHandler.lookupMethod(CommandManagerClass.commandManager, AddCommandMethod.addCommand);
		for (Class<?> cls : commands) {
			Class<?> cls2 = loader.loadClass(cls.getName());
			addCommandMethod.invoke(Feather.commandManager, cls2.newInstance());
		}

		LogManager.getLogger().info("Feather successfully bootstrapped");

		server.addPlayerListener(new PlayerListener() {
			public void playerJoined(Player player) {
				System.out.println(player.getName() + " joined");
			}

			public void playerLeft(Player player) {
				System.out.println(player.getName() + " left");
			}
		});
	}

	public static void playerJoined(Object playerEntity) {
		Entity entity = new Entity(playerEntity);
		Player player = server.loadPlayer(entity.getName());
		player.instance = playerEntity;
		server.playerJoined(player);
	}

	public static void playerLeft(Object playerEntity) {
		Entity entity = new Entity(playerEntity);
		Player player = server.getPlayer(entity.getName());
		if (player != null) server.playerLeft(player);
		server.unloadPlayer(entity.getName());
	}
}
