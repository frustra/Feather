package org.frustra.featherweight;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.frustra.featherweight.commands.TestCommand;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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
		loadHooks(loader);
		
		Class<?>[] commands = new Class<?>[] {
			TestCommand.class
		};
		for (Class<?> cls : commands) {
			InputStream classStream = FeatherWeight.class.getResourceAsStream("/" + cls.getName().replace('.', '/') + ".class");
			CustomClassNode node = new CustomClassNode();
			ClassReader reader = new ClassReader(classStream);
			reader.accept(node, 0);
			loader.commandClasses.put(cls.getName(), node);
		}

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
				loader.commandManagerClass = node;
			} else if (node.constants.contains("commands.help.usage")) {
				helpCommandClass = node;
			} else if (node.constants.contains("Rcon")) {
				loader.rconSessionClass = node;
			}
		}
		loader.baseCommandClass = loader.moddedClasses.get(helpCommandClass.superName.replace('/', '.'));
		String commandManagerInterfaceName = (String) loader.commandManagerClass.interfaces.get(0);
		String baseCommandInterfaceName = (String) loader.baseCommandClass.interfaces.get(0);
		String commandSessionInterfaceName = (String) loader.rconSessionClass.interfaces.get(0);
		
		for (MethodNode method : (List<MethodNode>) loader.commandManagerClass.methods) {
			Type[] args = Type.getArgumentTypes(method.desc);
			if (args.length == 1 && args[0].getClassName().equals(baseCommandInterfaceName)) {
				loader.addCommandMethod = method;
				break;
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
			} else if (args.length == 1 && args[0].getInternalName().equals(commandSessionInterfaceName) && ret.equals(Type.BOOLEAN_TYPE)) {
				loader.hasPermissionMethod = method;
			} else if (args.length == 2 && args[0].getInternalName().equals(commandSessionInterfaceName) && args[1].equals(Type.getType(String[].class)) && ret.equals(Type.VOID_TYPE)) {
				loader.executeCommandMethod = method;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addCommands(Object minecraftServer) throws Exception {
		CustomClassLoader loader = (CustomClassLoader) minecraftServer.getClass().getClassLoader();
		
		Class<?> minecraftServerClass = loader.loadClass("net.minecraft.server.MinecraftServer");
		Field commandManagerField = minecraftServerClass.getDeclaredField(loader.commandManagerField.name);
		commandManagerField.setAccessible(true);
		Object commandManager = commandManagerField.get(minecraftServer);

		Class<?> commandManagerClass = loader.loadClass(loader.commandManagerClass.name.replace('/', '.'));
		Type[] args = Type.getArgumentTypes(loader.addCommandMethod.desc);
		Method addCommandMethod = commandManagerClass.getDeclaredMethod(loader.addCommandMethod.name, loader.loadClass(args[0].getClassName()));
		addCommandMethod.setAccessible(true);
		
		for (CustomClassNode node : loader.commandClasses.values()) {
			node.superName = loader.baseCommandClass.name;
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("getName")) {
					method.name = loader.getCommandNameMethod.name;
					method.desc = loader.getCommandNameMethod.desc;
				} else if (method.name.equals("hasPermission")) {
					method.name = loader.hasPermissionMethod.name;
					method.desc = loader.hasPermissionMethod.desc;
				} else if (method.name.equals("execute")) {
					method.name = loader.executeCommandMethod.name;
					method.desc = loader.executeCommandMethod.desc;
				} else if (method.name.equals("<init>")) {
					method.instructions.clear();
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, loader.baseCommandClass.name, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.RETURN));
				}
			}
			
			Class<?> cls = loader.loadClass(node.name.replace('/', '.'));
			addCommandMethod.invoke(commandManager, cls.newInstance());
		}
	}
}
