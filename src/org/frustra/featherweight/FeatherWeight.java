package org.frustra.featherweight;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.frustra.featherweight.commands.TestCommand;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FeatherWeight {
	public static final String version = "1.0.0";
	public static Object minecraftServer = null;
	public static Object commandManager = null;
	public static CustomClassLoader loader = null;

	public static void main(String[] args) throws Exception {
		System.out.println("FeatherWeight v" + FeatherWeight.version);

		File minecraftServer = new File("lib/minecraft_server.jar");
		if (!minecraftServer.exists()) {
			System.err.println("Minecraft server jar is missing");
			return;
		}

		CustomClassLoader loader = new CustomClassLoader(minecraftServer);

		loader.loadJar();
		loadHooks(loader);

		Class<?>[] commands = new Class<?>[] { TestCommand.class };
		for (Class<?> cls : commands) {
			loader.commandClasses.put(cls.getName(), loadOwnClass(cls.getName()));
		}
		loader.moddedClasses.put(Entity.class.getName(), loadOwnClass(Entity.class.getName()));

		Thread.currentThread().setContextClassLoader(loader);

		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] { String[].class });
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] { args });
	}

	@SuppressWarnings("unchecked")
	public static void loadHooks(CustomClassLoader loader) {
		CustomClassNode helpCommandClass = null, rconEntityClass = null;
		for (CustomClassNode node : loader.moddedClasses.values()) {
			if (node.constants.contains("Couldn't process command")) {
				loader.commandManagerClass = node;
			} else if (node.constants.contains("commands.help.usage")) {
				helpCommandClass = node;
			} else if (node.constants.contains("Rcon")) {
				rconEntityClass = node;
			}
		}
		loader.baseCommandClass = loader.moddedClasses.get(helpCommandClass.superName.replace('/', '.'));
		String commandManagerInterfaceName = (String) loader.commandManagerClass.interfaces.get(0);
		String baseCommandInterfaceName = (String) loader.baseCommandClass.interfaces.get(0);
		String commandEntityInterfaceName = (String) rconEntityClass.interfaces.get(0);
		loader.commandEntityInterface = loader.moddedClasses.get(commandEntityInterfaceName);

		for (MethodNode method : (List<MethodNode>) loader.commandManagerClass.methods) {
			Type[] args = Type.getArgumentTypes(method.desc);
			Type ret = Type.getReturnType(method.desc);
			if (args.length == 1 && args[0].getClassName().equals(baseCommandInterfaceName)) {
				loader.addCommandMethod = method;
			} else if (ret.equals(Type.INT_TYPE) && args.length == 2 && args[0].getClassName().equals(commandEntityInterfaceName) && args[1].equals(Type.getType(String.class))) {
				loader.executeCommandMethod = method;
			}
		}

		CustomClassNode minecraftServer = loader.moddedClasses.get("net.minecraft.server.MinecraftServer");
		for (FieldNode field : (List<FieldNode>) minecraftServer.fields) {
			if (Type.getType(field.desc).getClassName().equals(commandManagerInterfaceName)) {
				loader.commandManagerField = field;
				break;
			}
		}

		CustomClassNode baseCommandInterface = loader.moddedClasses.get(baseCommandInterfaceName.replace('/', '.'));
		for (MethodNode method : (List<MethodNode>) baseCommandInterface.methods) {
			Type[] args = Type.getArgumentTypes(method.desc);
			Type ret = Type.getReturnType(method.desc);
			if (args.length == 0 && ret.equals(Type.getType(String.class))) {
				loader.getCommandNameMethod = method;
			} else if (args.length == 1 && args[0].getInternalName().equals(commandEntityInterfaceName) && ret.equals(Type.BOOLEAN_TYPE)) {
				loader.hasPermissionMethod = method;
			} else if (args.length == 2 && args[0].getInternalName().equals(commandEntityInterfaceName) && args[1].equals(Type.getType(String[].class)) && ret.equals(Type.VOID_TYPE)) {
				loader.handleExecuteMethod = method;
			}
		}
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
		FeatherWeight.minecraftServer = minecraftServer;
		Injection.injectServer(minecraftServer);
	}
}
