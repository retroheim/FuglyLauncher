package net.teamfruit.skcraft.launcher.dirs;

import java.io.File;

public abstract class OptionLauncherDirectories extends AbstractLauncherDirectories {

	public abstract String getPathCommonDataDir();

	public abstract String getPathInstancesDir();

	public File getDefaultCommonDataDir() {
		return super.getCommonDataDir();
	}

	public File getDefaultInstancesDir() {
		return super.getInstancesDir();
	}

	@Override
	public File getCommonDataDir() {
		return DirectoryUtils.getDirFromOption(getDefaultCommonDataDir(), getPathCommonDataDir());
	}

	@Override
	public File getInstancesDir() {
		return DirectoryUtils.getDirFromOption(getDefaultInstancesDir(), getPathInstancesDir());
	}

}
