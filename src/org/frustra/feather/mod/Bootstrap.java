package org.frustra.feather.mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.frustra.feather.Feather;
import org.frustra.feather.hooks.AddCommandMethod;
import org.frustra.feather.hooks.CommandManagerClass;
import org.frustra.feather.hooks.MinecraftServerClass;
import org.frustra.feather.mod.commands.Command;
import org.frustra.feather.mod.logging.LogManager;
import org.frustra.feather.mod.server.Entity;
import org.frustra.feather.mod.server.Player;
import org.frustra.feather.mod.server.PlayerListener;
import org.frustra.feather.mod.server.Server;
import org.frustra.filament.hooking.HookingHandler;

public class Bootstrap {
	public static Object minecraftServer = null;
	public static Object commandManager = null;
	public static Server server = null;

	public static void bootstrap(Object minecraftServer) throws Exception {
		Thread.currentThread().setName("Feather");

		Bootstrap.server = new Server();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				this.setName("Feather");
				LogManager.getLogger().info("Feather shutting down");
				Bootstrap.server.shutdown();
			}
		});

		Field commandManagerField = HookingHandler.lookupField(MinecraftServerClass.minecraftServer, MinecraftServerClass.commandManager);
		Bootstrap.commandManager = commandManagerField.get(minecraftServer);
		Bootstrap.minecraftServer = minecraftServer;

		Method addCommandMethod = HookingHandler.lookupMethod(CommandManagerClass.commandManager, AddCommandMethod.addCommand);
		for (String name : Feather.loader.listPackage("org.frustra.feather.mod.commands")) {
			if (name.equals(Command.class.getName())) continue;
			Class<?> cls = Feather.loader.loadClass(name);
			addCommandMethod.invoke(Bootstrap.commandManager, cls.newInstance());
		}

		LogManager.getLogger().info("Feather successfully bootstrapped");

		Bootstrap.server.addPlayerListener(new PlayerListener() {
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
		server.playerJoined(entity);
	}

	public static void playerLeft(Object playerEntity) {
		Entity entity = new Entity(playerEntity);
		server.playerLeft(entity);
	}
}
