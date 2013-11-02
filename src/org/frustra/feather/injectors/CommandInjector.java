package org.frustra.feather.injectors;

import org.frustra.feather.Entity;
import org.frustra.feather.hooks.HandleExecuteCommandMethod;
import org.frustra.feather.hooks.HasCommandPermissionMethod;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.injection.ClassInjector;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CommandInjector extends ClassInjector {
	public boolean match(CustomClassNode node) {
		return node.superName.equals("org/frustra/featherweight/Command");
	}

	@SuppressWarnings("unchecked")
	public void inject(CustomClassNode node) {
		String entityClassName = Entity.class.getName().replace('.', '/');

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
	}

	public static MethodNode cloneMethodNode(MethodNode s) {
		@SuppressWarnings("unchecked")
		MethodNode d = new MethodNode(s.access & ~Opcodes.ACC_ABSTRACT, s.name, s.desc, s.signature, (String[]) s.exceptions.toArray(new String[0]));
		return d;
	}
}
