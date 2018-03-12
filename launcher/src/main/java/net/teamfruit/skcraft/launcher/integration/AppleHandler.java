package net.teamfruit.skcraft.launcher.integration;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.OpenURIHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.skcraft.launcher.util.Environment;
import com.skcraft.launcher.util.Platform;

public class AppleHandler {
	private static class AppleHandlerImpl implements OpenFilesHandler, AboutHandler, PreferencesHandler, QuitHandler, OpenURIHandler {

		@Override
		public void openFiles(AppEvent.OpenFilesEvent ofe) {

		}

		@Override
		public void handleAbout(AppEvent.AboutEvent ae) {

		}

		@Override
		public void handlePreferences(AppEvent.PreferencesEvent pe) {

		}

		@Override
		public void handleQuitRequestWith(AppEvent.QuitEvent qe, QuitResponse qr) {

		}

		@Override
		public void openURI(AppEvent.OpenURIEvent oue) {

		}

		public void register() {
			Application app = Application.getApplication();
			app.setAboutHandler(this);
			app.setPreferencesHandler(this);
			app.setQuitHandler(this);
			app.setOpenFileHandler(this);
			app.setOpenURIHandler(this);
		}

	}

	public static void register() {
		if (Environment.getInstance().getPlatform()==Platform.MAC_OS_X)
			try {
				new AppleHandlerImpl().register();
			} catch (Throwable ignored) {
				ignored.printStackTrace();
			}
	}
}