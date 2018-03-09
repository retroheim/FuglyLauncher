package net.teamfruit.skcraft.launcher.integration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.JCommander;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.LauncherArguments;
import com.skcraft.launcher.launch.JavaProcessBuilder;
import com.skcraft.launcher.launch.JavaRuntimeFinder;
import com.skcraft.launcher.util.Environment;
import com.skcraft.launcher.util.Platform;
import com.skcraft.launcher.util.WinRegistry;

import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.relaunch.LauncherBinary;

@Log
public class UriScheme {
	private final Launcher launcher;

	public UriScheme(Launcher launcher) {
		this.launcher = launcher;
	}

	public File getBinary() {
		File[] files = launcher.getLauncherBinariesDir().listFiles(new LauncherBinary.Filter());
		List<LauncherBinary> binaries = new ArrayList<LauncherBinary>();

		if (files!=null)
			for (File file : files) {
				log.info("Found "+file.getAbsolutePath()+"...");
				binaries.add(new LauncherBinary(file));
			}

		if (!binaries.isEmpty()) {
			Collections.sort(binaries);

			for (LauncherBinary binary : binaries) {
				try {
					File testFile = binary.getExecutableJar();
					log.info("Loaded entry point "+testFile.getAbsolutePath()+"...");
					return testFile;
				} catch (Throwable t) {
				}
			}
		}

		return null;
	}

	public void install() {
		if (Environment.getInstance().getPlatform()!=Platform.WINDOWS)
			return;

		File iconPath = new File(launcher.getBaseDir(), "launcher.ico");
		try {
			Files.copy(new InputSupplier<InputStream>() {
				@Override
				public InputStream getInput() throws IOException {
					return AppIcon.getWindowsAppIcon();
				}
			}, iconPath);
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}

		try {
			File jvmPath = JavaRuntimeFinder.findBestJavaPath();
			File binaryPath = getBinary();
			if (jvmPath==null||binaryPath==null)
				return; // TODO

			Class<?> clazz = load(binaryPath);

			JavaProcessBuilder builder = new JavaProcessBuilder();
			builder.setJvmPath(jvmPath);
			builder.setJvmApp("javaw");
			builder.classPath(binaryPath.getCanonicalFile());
			builder.setMainClass(clazz.getName());

	        LauncherArguments options = new LauncherArguments();
	        JCommander cmd = new JCommander(options);
	        cmd.setAcceptUnknownOptions(true);
	        cmd.parse(launcher.getArgs());

	        List<String> arguments = Lists.newArrayList();
	        arguments.add("--dir");
	        arguments.add(launcher.getBaseDir().getCanonicalPath());
	        Integer bsVersion = options.getBootstrapVersion();
	        if (bsVersion!=null) {
		        arguments.add("--bootstrap-version");
		        arguments.add(String.valueOf(bsVersion));
	        }
	        arguments.add("--uripath");
	        arguments.add("\"%1\"");
	        arguments.addAll(cmd.getUnknownOptions());
	        builder.getArgs().addAll(arguments);

			log.info("Installing URL Protocol...");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\DefaultIcon");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\shell\\open\\command");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher", "", "URL:FruitLauncher Protocol");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher", "URL Protocol", "");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\DefaultIcon", "", iconPath.getCanonicalPath());
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\shell\\open\\command", "", joinCommand(builder.buildCommand()));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}

	public String joinCommand(List<String> arguments) {
		List<String> args = Lists.newArrayList(arguments);
		for (ListIterator<String> itr = args.listIterator(); itr.hasNext();) {
			String arg = itr.next();
			if (!(arg.startsWith("\"")&&arg.endsWith("\""))&&arg.contains(" "))
				itr.set("\""+arg+"\"");
		}
	    return StringUtils.join(args, " ");
	}

	public Class<?> load(File jarFile) throws MalformedURLException, ClassNotFoundException {
        URL[] urls = new URL[] { jarFile.toURI().toURL() };
        URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());
        Class<?> clazz = Class.forName(launcher.getMainClass().getName(), true, child);
        return clazz;
    }
}
