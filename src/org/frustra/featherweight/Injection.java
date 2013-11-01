package org.frustra.featherweight;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Injection {
	
	public static MethodNode cloneMethodNode(MethodNode s) {
		MethodNode d = new MethodNode(Opcodes.ASM4, s.access & ~Opcodes.ACC_ABSTRACT, s.name, s.desc, s.signature, null);
		return d;
	}
	
	@SuppressWarnings("unchecked")
	public static void injectNode(CustomClassNode node, CustomClassLoader loader) {
		if (node.name.equals("net/minecraft/server/MinecraftServer")) {
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("<init>")) {
					InsnList iList = new InsnList();
					iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FeatherWeight.class.getName().replace('.', '/'), "bootstrap", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.getType(Object.class)})));
					method.instructions.insertBefore(method.instructions.getLast(), iList);
					break;
				}
			}
		} else if (node.name.equals(Entity.class.getName().replace('.', '/'))) {
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("getName")) {
					method.instructions.clear();
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, node.name, "entity", Type.getDescriptor(Object.class)));
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, loader.commandEntityInterface.name, "b_", Type.getMethodDescriptor(Type.getType(String.class), new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.ARETURN));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void injectServer(Object minecraftServer) throws Exception {
		CustomClassLoader loader = FeatherWeight.loader;
		Class<?> minecraftServerClass = loader.loadClass("net.minecraft.server.MinecraftServer");
		Field commandManagerField = minecraftServerClass.getDeclaredField(loader.commandManagerField.name);
		commandManagerField.setAccessible(true);
		FeatherWeight.commandManager = commandManagerField.get(minecraftServer);
	
		Class<?> commandManagerClass = loader.loadClass(loader.commandManagerClass.name.replace('/', '.'));
		Type[] args = Type.getArgumentTypes(loader.addCommandMethod.desc);
		Method addCommandMethod = commandManagerClass.getDeclaredMethod(loader.addCommandMethod.name, loader.loadClass(args[0].getClassName()));
		addCommandMethod.setAccessible(true);
		
		String entityClassName = Entity.class.getName().replace('.', '/');
		
		for (CustomClassNode node : loader.commandClasses.values()) {
			node.superName = loader.baseCommandClass.name;
			
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("getName")) {
					method.name = loader.getCommandNameMethod.name;
					method.desc = loader.getCommandNameMethod.desc;
				} else if (method.name.equals("<init>")) {
					method.instructions.clear();
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, loader.baseCommandClass.name, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.RETURN));
				}
			}
			
			MethodNode executeProxy = cloneMethodNode(loader.handleExecuteMethod);
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			executeProxy.instructions.add(new TypeInsnNode(Opcodes.NEW, entityClassName));
			executeProxy.instructions.add(new InsnNode(Opcodes.DUP));
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			executeProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entityClassName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.getType(Object.class)})));
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
			executeProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, node.name, "execute", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.getType(Entity.class), Type.getType(String[].class)})));
			executeProxy.instructions.add(new InsnNode(Opcodes.RETURN));
			node.methods.add(executeProxy);
			
			MethodNode hasPermissionProxy = cloneMethodNode(loader.hasPermissionMethod);
			hasPermissionProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			hasPermissionProxy.instructions.add(new TypeInsnNode(Opcodes.NEW, entityClassName));
			hasPermissionProxy.instructions.add(new InsnNode(Opcodes.DUP));
			hasPermissionProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			hasPermissionProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entityClassName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.getType(Object.class)})));
			hasPermissionProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, node.name, "hasPermission", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] {Type.getType(Entity.class)})));
			hasPermissionProxy.instructions.add(new InsnNode(Opcodes.IRETURN));
			node.methods.add(hasPermissionProxy);
			
			Class<?> cls = loader.loadClass(node.name.replace('/', '.'));
			addCommandMethod.invoke(FeatherWeight.commandManager, cls.newInstance());
		}
	}
}
