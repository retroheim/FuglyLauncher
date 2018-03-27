package net.teamfruit.skcraft.launcher.skins;

import java.awt.Image;
import java.util.ResourceBundle;

import com.skcraft.launcher.Launcher;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSkin implements Skin {
	private final Launcher launcher;

	@Override
	public String getNewsURL() {
		return launcher.getProperties().getProperty("newsUrl");
	}

	@Override
	public String getTipsURL() {
		return launcher.getProperties().getProperty("tipsUrl");
	}

	@Override
	public String getSupportURL() {
		return launcher.getProperties().getProperty("supportUrl");
	}

	@Override
	public ResourceBundle getLang() {
		return null;
	}

	@Override
	public Image getBackgroundImage() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void downlaodResources() throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

}
