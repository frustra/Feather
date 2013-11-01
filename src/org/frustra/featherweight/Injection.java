package org.frustra.featherweight;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.frustra.featherweight.hooks.AddCommandMethod;
import org.frustra.featherweight.hooks.CommandManagerClass;
import org.frustra.featherweight.hooks.GetCommandNameMethod;
import org.frustra.featherweight.hooks.HandleExecuteCommandMethod;
import org.frustra.featherweight.hooks.HasCommandPermissionMethod;
import org.frustra.featherweight.hooks.HelpCommandClass;
import org.frustra.featherweight.hooks.MinecraftServerClass;
import org.frustra.featherweight.hooks.RconEntityClass;
import org.frustra.filament.hooking.CustomClassNode;
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
					iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FeatherWeight.class.getName().replace('.', '/'), "bootstrap", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
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
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, RconEntityClass.commandEntity.name, "b_", Type.getMethodDescriptor(Type.getType(String.class), new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.ARETURN));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void injectServer(Object minecraftServer) throws Exception {
		CustomClassLoader loader = FeatherWeight.loader;
		Class<?> minecraftServerClass = loader.loadClass("net.minecraft.server.MinecraftServer");
		Field commandManagerField = minecraftServerClass.getDeclaredField(MinecraftServerClass.commandManager.name);
		commandManagerField.setAccessible(true);
		FeatherWeight.commandManager = commandManagerField.get(minecraftServer);

		Class<?> commandManagerClass = loader.loadClass(CommandManagerClass.commandManager.name.replace('/', '.'));
		Type[] args = Type.getArgumentTypes(AddCommandMethod.addCommand.desc);
		Method addCommandMethod = commandManagerClass.getDeclaredMethod(AddCommandMethod.addCommand.name, loader.loadClass(args[0].getClassName()));
		addCommandMethod.setAccessible(true);

		String entityClassName = Entity.class.getName().replace('.', '/');

		for (CustomClassNode node : loader.store.filament.classes.values()) {
			if (!node.superName.equals("org/frustra/featherweight/Command")) continue;
			node.superName = HelpCommandClass.baseCommand.name;

			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("getName")) {
					method.name = GetCommandNameMethod.getName.name;
					method.desc = GetCommandNameMethod.getName.desc;
				} else if (method.name.equals("<init>")) {
					method.instructions.clear();
					method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, HelpCommandClass.baseCommand.name, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[0])));
					method.instructions.add(new InsnNode(Opcodes.RETURN));
				}
			}

			MethodNode executeProxy = cloneMethodNode(HandleExecuteCommandMethod.handleExecute);
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			executeProxy.instructions.add(new TypeInsnNode(Opcodes.NEW, entityClassName));
			executeProxy.instructions.add(new InsnNode(Opcodes.DUP));
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			executeProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entityClassName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
			executeProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
			executeProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, node.name, "execute", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Entity.class), Type.getType(String[].class) })));
			executeProxy.instructions.add(new InsnNode(Opcodes.RETURN));
			node.methods.add(executeProxy);

			MethodNode hasPermissionProxy = cloneMethodNode(HasCommandPermissionMethod.hasPermission);
			hasPermissionProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			hasPermissionProxy.instructions.add(new TypeInsnNode(Opcodes.NEW, entityClassName));
			hasPermissionProxy.instructions.add(new InsnNode(Opcodes.DUP));
			hasPermissionProxy.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			hasPermissionProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, entityClassName, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(Object.class) })));
			hasPermissionProxy.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, node.name, "hasPermission", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] { Type.getType(Entity.class) })));
			hasPermissionProxy.instructions.add(new InsnNode(Opcodes.IRETURN));
			node.methods.add(hasPermissionProxy);

			Class<?> cls = loader.loadClass(node.name.replace('/', '.'));
			addCommandMethod.invoke(FeatherWeight.commandManager, cls.newInstance());
		}
	}
}
