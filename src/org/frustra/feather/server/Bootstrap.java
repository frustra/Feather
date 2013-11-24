package org.frustra.feather.server;

import java.lang.reflect.Field;

import org.frustra.feather.Feather;
import org.frustra.feather.server.logging.LogManager;
import org.frustra.filament.HookUtil;

public class Bootstrap {
	public static Object minecraftServer = null;
	public static Object commandManager = null;
	public static Server server = null;

	public static void bootstrap(Object minecraftServer) throws Exception {
		Thread.currentThread().setName("Feather");

		Field commandManagerField = HookUtil.lookupField("MinecraftServer.commandManager");
		Bootstrap.commandManager = commandManagerField.get(minecraftServer);
		Bootstrap.minecraftServer = minecraftServer;

		for (String name : Feather.loader.listPackage("org.frustra.feather.server.commands")) {
			if (name.endsWith("Command") && !name.equals("Command")) {
				Class<?> cls = Feather.loader.loadClass(name);
				Command.addCommand((Command) cls.newInstance());
			}
		}

		Bootstrap.server = new Server();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				this.setName("Feather");
				LogManager.getLogger().info("Feather shutting down");
				Bootstrap.server.shutdown();
			}
		});

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

	public static void worldLoaded() {
		server.ready();
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
