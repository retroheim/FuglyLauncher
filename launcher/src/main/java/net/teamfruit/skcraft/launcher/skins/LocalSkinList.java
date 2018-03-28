package net.teamfruit.skcraft.launcher.skins;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.skcraft.launcher.Launcher;

import lombok.NonNull;

public class LocalSkinList {

	private final Launcher launcher;

	/**
	 * Create a new skin list.
	 *
	 * @param launcher the launcher
	 */
	public LocalSkinList(@NonNull final Launcher launcher) {
		this.launcher = launcher;
	}

	/**
	 * Get the local skin
	 *
	 * @return the local skin
	 */
	public @Nullable LocalSkin getLocalSkin(String name) {
		if (!StringUtils.isEmpty(name)) {
			LocalSkin skin = new LocalSkin(launcher, name);
			if (skin.exists())
				return skin;
		}
		return null;
	}

	/*	public LocalSkinList enumerate() {
			log.info("Enumerating local skins list...");

			final List<LocalSkin> local = Lists.newArrayList();

			try {
				File skinDir = launcher.getSkinDir();
				File[] files = skinDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});

				for (File file: files) {
					LocalSkin skin = new LocalSkin(file);
					if (skin.getFile().exists())
						local.add(skin);
				}
			} finally {
				synchronized (this) {
					localSkins.clear();
					localSkins.addAll(local);

					log.info(localSkins.size()+" local skin(s) enumerated.");
				}
			}
			return this;
		}*/
}