package net.teamfruit.skcraft.launcher.skins;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.internal.Nullable;
import com.google.common.io.Closer;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.LauncherUtils;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.HttpRequest;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.model.skins.SkinInfo;

@Log
@RequiredArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true, of = { "skinInfo", "resourceDir" })
public class SkinData implements Skin {
	private final Launcher launcher;
	private final Skin defaultSkin;
	@Nullable
	private final SkinInfo skinInfo;
	private final File resourceDir;

	@Override
	public String getNewsURL() {
		String data = null;
		if (skinInfo!=null)
			data = skinInfo.getNewsURL();
		if (data==null)
			data = defaultSkin.getNewsURL();
		return data;
	}

	@Override
	public String getTipsURL() {
		String data = null;
		if (skinInfo!=null)
			data = skinInfo.getTipsURL();
		if (data==null)
			data = defaultSkin.getTipsURL();
		return data;
	}

	@Override
	public String getSupportURL() {
		String data = null;
		if (skinInfo!=null)
			data = skinInfo.getSupportURL();
		if (data==null)
			data = defaultSkin.getSupportURL();
		return data;
	}

	private ResourceBundle lang;
	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final ResourceBundle backingLang = loadLang();

	@Override
	public ResourceBundle getLang() {
		if (skinInfo!=null)
			if (lang==null)
				lang = getBackingLang();
		if (lang==null)
			return defaultSkin.getLang();
		return lang;
	}

	private ResourceBundle loadLang() {
		File langFile = getLangFile();
		if (langFile.isFile())
			try {
				return new PropertyResourceBundle(new InputStreamReader(new FileInputStream(langFile), "UTF-8"));
			} catch (Exception e) {
				log.log(Level.WARNING, "Could not load skin lang file: ", e);
			}
		return null;
	}

	private File getLangFile() {
		return new File(resourceDir, "lang.properties");
	}

	@Getter(lazy = true, value = AccessLevel.PRIVATE)
	private final Image backingBackgroundImage = loadBackgroundImage();

	@Override
	public Image getBackgroundImage() {
		Image data = null;
		if (skinInfo!=null)
			data = getBackingBackgroundImage();
		if (data==null)
			data = defaultSkin.getBackgroundImage();
		return data;
	}

	private Image loadBackgroundImage() {
		String url = skinInfo.getBackgroundURL();
		if (!StringUtils.isEmpty(url))
			return SwingHelper.createImage(url);
		return null;
	}

	@Override
	public boolean isShowList() {
		boolean data = defaultSkin.isShowList();
		if (skinInfo!=null)
			data = skinInfo.isShowList();
		return data;
	}

	@Override
	public String getDefaultModPack() {
		String data = null;
		if (skinInfo!=null)
			data = skinInfo.getDefaultModPack();
		if (data==null)
			data = defaultSkin.getDefaultModPack();
		return data;
	}

	@Override
	public void downloadResources() throws Exception {
		if (skinInfo==null) {
			defaultSkin.downloadResources();
			return;
		}

		resourceDir.mkdirs();
		String langURL = skinInfo.getLangURL();
		if (!StringUtils.isEmpty(langURL)) {
			byte[] bytes = HttpRequest
					.get(HttpRequest.url(langURL))
					.execute()
					.expectResponseCode(200)
					.returnContent()
					.saveContent(getLangFile())
					.asBytes();
			Closer closer = Closer.create();
			try {
				lang = new PropertyResourceBundle(closer.register(new InputStreamReader(new ByteArrayInputStream(bytes), "UTF-8")));
			} finally {
				LauncherUtils.closeQuietly(closer);
			}
		}
	}

}
