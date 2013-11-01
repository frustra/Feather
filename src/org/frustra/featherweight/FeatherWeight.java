package org.frustra.featherweight;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FeatherWeight {
	public static final String version = "1.0.0";

	public static CustomClassNode commandManagerClass = null;
	public static CustomClassNode baseCommandClass = null;
	public static MethodNode addCommandMethod = null;
	public static FieldNode commandManagerField = null;
	
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

		Thread.currentThread().setContextClassLoader(loader);
		
		Class<?> cls = loader.loadClass("net.minecraft.server.MinecraftServer");
		Method entryPoint = cls.getDeclaredMethod("main", new Class[] {String[].class});
		entryPoint.setAccessible(true);
		entryPoint.invoke(null, new Object[] {args});
	}
	
	@SuppressWarnings("unchecked")
	public static void loadHooks(CustomClassLoader loader) {
		CustomClassNode helpCommandClass = null;
		for (CustomClassNode node : loader.moddedClasses.values()) {
			if (node.constants.contains("Couldn't process command")) {
				commandManagerClass = node;
			} else if (node.constants.contains("commands.help.usage")) {
				helpCommandClass = node;
			}
		}
		baseCommandClass = loader.moddedClasses.get(helpCommandClass.superName.replace('/', '.'));
		String commandManagerInterface = (String) commandManagerClass.interfaces.get(0);
		String baseCommandInterface = (String) baseCommandClass.interfaces.get(0);
		
		for (MethodNode method : (List<MethodNode>) commandManagerClass.methods) {
			Type[] args = Type.getArgumentTypes(method.desc);
			if (args.length == 1 && args[0].getClassName().equals(baseCommandInterface)) {
				addCommandMethod = method;
				break;
			}
		}
		
		CustomClassNode minecraftServer = loader.moddedClasses.get("net.minecraft.server.MinecraftServer");
		for (FieldNode field : (List<FieldNode>) minecraftServer.fields) {
			if (Type.getType(field.desc).getClassName().equals(commandManagerInterface)) {
				commandManagerField = field;
				break;
			}
		}
	}
	
	public static void addCommands(Object minecraftServer) {
		
		System.out.println(minecraftServer.getClass().getName());
	}
}
