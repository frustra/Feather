package org.frustra.featherweight;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CustomClassLoader extends URLClassLoader {
	public HashMap<String, CustomClassNode> moddedClasses = new HashMap<String, CustomClassNode>();
	public HashMap<String, CustomClassNode> commandClasses = new HashMap<String, CustomClassNode>();
	public JarFile jarFile;
	public File jarPath;

	public CustomClassNode commandManagerClass = null;
	public CustomClassNode baseCommandClass = null;
	public CustomClassNode rconSessionClass = null;
	public MethodNode addCommandMethod = null;
	public FieldNode commandManagerField = null;
	public MethodNode getCommandNameMethod = null;
	public MethodNode hasPermissionMethod = null;
	public MethodNode executeCommandMethod = null;
	
	public CustomClassLoader(File jarPath) throws IOException {
		super(new URL[] {jarPath.toURI().toURL()});
		this.jarPath = jarPath;
		this.jarFile = new JarFile(jarPath);
	}
	
	public Class<?> getPrimitiveType(String name) throws ClassNotFoundException {
		if (name.equals("byte")) return byte.class;
		if (name.equals("short")) return short.class;
		if (name.equals("int")) return int.class;
		if (name.equals("long")) return long.class;
		if (name.equals("char")) return char.class;
		if (name.equals("float")) return float.class;
		if (name.equals("double")) return double.class;
		if (name.equals("boolean")) return boolean.class;
		if (name.equals("void")) return void.class;
		// new ClassNotFoundException(name).printStackTrace();
		throw new ClassNotFoundException(name);
	}
	
	HashMap<String, Class<?>> loaded = new HashMap<String, Class<?>>();
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> cls = loaded.get(name);
		if (cls == null) {
			cls = defineClass(name);
			if (cls != null) loaded.put(name, cls);
		}
		return cls;
	}
	
	private Class<?> defineClass(String name) throws ClassNotFoundException {
		if (name == null) return null;
		try {
			byte[] buf = getClassBytes(name);
			if (buf != null) {
				return defineClass(name, buf, 0, buf.length);
			} else {
				try {
					return super.loadClass(name);
				} catch (Exception e1) {
					return getPrimitiveType(name);
				}
			}
		} catch (Exception e) {
			throw new ClassNotFoundException(name, e);
		}
	}
	
	public byte[] getClassBytes(String name) {
		CustomClassNode node = moddedClasses.get(name);
		if (node == null) node = commandClasses.get(name);
		
		if (node != null) {
			doInjection(node);
			
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			
			return writer.toByteArray();
		} else return null;
	}
	
	public InputStream getResourceAsStream(String name) {
		if (name.endsWith(".class")) {
			byte[] buf = getClassBytes(name.substring(0, name.length() - 6).replace('/', '.'));
			if (buf != null) return new ByteArrayInputStream(buf);
		}
		return super.getResourceAsStream(name);
	}
	
	public URL findResource(String name) {
		byte[] buf = null;
		if (name.endsWith(".class")) buf = getClassBytes(name.substring(0, name.length() - 6).replace('/', '.'));
		if (buf == null) {
			return super.findResource(name);
		}
		final InputStream stream = new ByteArrayInputStream(buf);
		URLStreamHandler handler = new URLStreamHandler() {
			protected URLConnection openConnection(URL url) throws IOException {
				return new URLConnection(url) {
					public void connect() throws IOException {}
					
					public InputStream getInputStream() {
						return stream;
					}
				};
			}
		};
		try {
			return new URL(new URL("http://www.frustra.org/"), "", handler);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadJar() {
		try {
			moddedClasses.clear();
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry != null && entry.getName().endsWith(".class")) {
					CustomClassNode node = new CustomClassNode();
					ClassReader reader = new ClassReader(jarFile.getInputStream(entry));
					reader.accept(node, ClassReader.SKIP_DEBUG);
					char[] buf = new char[reader.getMaxStringLength()];
					for (int i = 0; i < reader.getItemCount(); i++) {
						try {
							Object constant = reader.readConst(i, buf);
							if (constant instanceof String) {
								node.constants.add((String) constant);
							} else if (constant instanceof Type) {
								node.references.add((Type) constant);
							}
						} catch (Exception e) {}
					}
					node.access &= ~(Opcodes.ACC_FINAL | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE);
					node.access |= Opcodes.ACC_PUBLIC;
					String name = node.name.replaceAll("/", ".");
					moddedClasses.put(name, node);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doInjection(CustomClassNode node) {
		if (node.name.equals("net/minecraft/server/MinecraftServer")) {
			for (MethodNode method : (List<MethodNode>) node.methods) {
				if (method.name.equals("<init>")) {
					InsnList iList = new InsnList();
					iList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					iList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, FeatherWeight.class.getName().replace('.', '/'), "addCommands", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.getType(Object.class)})));
					method.instructions.insertBefore(method.instructions.getLast(), iList);
					break;
				}
			}
		}
	}
}
