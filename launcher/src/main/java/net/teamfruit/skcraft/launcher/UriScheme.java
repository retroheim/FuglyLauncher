package net.teamfruit.skcraft.launcher;

import java.io.File;

import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.launch.JavaRuntimeFinder;
import com.skcraft.launcher.util.Environment;
import com.skcraft.launcher.util.Platform;
import com.skcraft.launcher.util.WinRegistry;

import lombok.extern.java.Log;

@Log
public class UriScheme {
	private final Launcher launcher;

	public UriScheme(Launcher launcher) {
		this.launcher = launcher;
	}

	public void install() {
		if ("".isEmpty())
			return;

		if (Environment.getInstance().getPlatform()!=Platform.WINDOWS)
			return;

		try {
			File jvmPath = JavaRuntimeFinder.findBestJavaPath();
			log.info("Installing URL Protocol...");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher");
			WinRegistry.createKey(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\shell\\open\\command");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher", "", "URL:FruitLauncher Protocol");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher", "URL Protocol", "");
			WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Classes\\fruitlauncher\\shell\\open\\command", "", jvmPath.getCanonicalPath()+"");
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}
}
