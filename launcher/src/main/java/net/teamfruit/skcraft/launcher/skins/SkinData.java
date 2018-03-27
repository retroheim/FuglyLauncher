package net.teamfruit.skcraft.launcher.skins;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.beust.jcommander.internal.Nullable;
import com.google.common.io.Closer;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.LauncherUtils;
import com.skcraft.launcher.util.HttpRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.model.skins.SkinInfo;

@Log
@RequiredArgsConstructor
public class SkinData implements Skin {
	private final Launcher launcher;
	@Nullable
	private final SkinInfo skinInfo;
	private final File resourceDir;

	@Override
	public String getNewsURL() {
		if (skinInfo==null)
			return null;
		return skinInfo.getNewsURL();
	}

	@Override
	public String getTipsURL() {
		if (skinInfo==null)
			return null;
		return skinInfo.getTipsURL();
	}

	@Override
	public String getSupportURL() {
		if (skinInfo==null)
			return null;
		return skinInfo.getSupportURL();
	}

	private ResourceBundle lang;

	@Override
	public ResourceBundle getLang() {
		if (skinInfo==null)
			return null;
		if (lang==null)
	        try {
				lang = new PropertyResourceBundle(new InputStreamReader(new FileInputStream(getLangFile()), "UTF-8"));
			} catch (Exception e) {
				log.log(Level.WARNING, "Could not load skin lang file: ", e);
			}
		return lang;
	}

	private File getLangFile() {
		return new File(resourceDir, "lang.properties");
	}

	@Override
	public Image getBackgroundImage() {
		if (skinInfo==null)
			return null;
		return null;
	}

	@Override
	public void downloadResources() throws Exception {
		resourceDir.mkdirs();
		byte[] bytes = HttpRequest
				.get(HttpRequest.url(skinInfo.getLangURL()))
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
