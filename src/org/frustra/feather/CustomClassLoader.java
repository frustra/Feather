package org.frustra.feather;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.jar.JarFile;

import org.frustra.filament.FilamentStorage;
import org.frustra.filament.hooking.CustomClassNode;
import org.frustra.filament.injection.InjectionHandler;
import org.objectweb.asm.ClassWriter;

public class CustomClassLoader extends URLClassLoader {
	public FilamentStorage filament;
	private ClassLoader parent;

	public JarFile jarFile;
	public File jarPath;

	public CustomClassLoader(File jarPath) throws IOException {
		super(new URL[] { jarPath.toURI().toURL() });
		this.jarPath = jarPath;
		this.jarFile = new JarFile(jarPath);
		this.filament = new FilamentStorage(this, Feather.debug);
		this.parent = CustomClassLoader.class.getClassLoader();
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
					try {
						return parent.loadClass(name);
					} catch (Exception e2) {
						return getPrimitiveType(name);
					}
				}
			}
		} catch (Exception e) {
			throw new ClassNotFoundException(name, e);
		}
	}

	public byte[] getClassBytes(String name) {
		CustomClassNode node = filament.classes.get(name);

		if (node != null) {
			InjectionHandler.doInjection(node);

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
		InputStream stream = null;
		try {
			stream = super.getResourceAsStream(name);
		} catch (Throwable e) {}
		if (stream != null) return stream;
		try {
			stream = parent.getResourceAsStream(name);
		} catch (Throwable e) {}
		return stream;
	}

	public URL findResource(String name) {
		byte[] buf = null;
		if (name.endsWith(".class")) buf = getClassBytes(name.substring(0, name.length() - 6).replace('/', '.'));
		URL url = null;
		if (buf == null) {
			try {
				url = super.findResource(name);
			} catch (Throwable e) {}
			if (url != null) return url;
			try {
				url = parent.getResource(name);
			} catch (Throwable e) {}
			return url;
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
}
