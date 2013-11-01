package org.frustra.featherweight;

import java.io.File;
import java.util.jar.JarFile;

import org.frustra.filament.FilamentStorage;


public class Storage {
	public CustomClassLoader classLoader = null;
	public FilamentStorage filament;

	public JarFile jarFile;
	public File jarPath;

	public Storage(CustomClassLoader loader) {
		this.classLoader = loader;
		loader.store = this;
		this.filament = new FilamentStorage(loader, FeatherWeight.debug);
	}
}
