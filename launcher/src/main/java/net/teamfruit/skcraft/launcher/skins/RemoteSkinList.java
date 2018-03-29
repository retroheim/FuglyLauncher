package net.teamfruit.skcraft.launcher.skins;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.skcraft.concurrency.DefaultProgress;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.util.HttpRequest;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
public class RemoteSkinList {

	private final Launcher launcher;
	@Getter private Properties skinProperties;

	/**
	 * Create a new skin list.
	 *
	 * @param launcher the launcher
	 */
	public RemoteSkinList(@NonNull final Launcher launcher) {
		this.launcher = launcher;
	}

	public RemoteSkin getRemoteSkin(String name) {
		if (name==null||skinProperties==null)
			return null;
		String url = skinProperties.getProperty(name);
		if (url==null)
			return null;
		return new RemoteSkin(launcher, name, url);
	}

	/**
	 * Create a worker that loads the list of skins from disk and from
	 * the remote list of packages.
	 *
	 * @return the worker
	 */
	public Enumerator createEnumerator() {
		return new Enumerator();
	}

	public final class Enumerator implements Callable<RemoteSkinList>, ProgressObservable {
		private ProgressObservable progress = new DefaultProgress(-1, null);

		private Enumerator() {
		}

		@Override
		public RemoteSkinList call() throws Exception {
			log.info("Enumerating skins list...");

			Properties skinsProp = new Properties();

			this.progress = new DefaultProgress(0.3, SharedLocale.tr("skins.checkingListRemote"));

			try {
				final URL skinsURL = RemoteSkinList.this.launcher.getSkinsURL();

				skinsProp = HttpRequest
						.get(skinsURL)
						.execute()
						.expectResponseCode(200)
						.returnContent()
						.asProperties();

			} catch (final IOException e) {
				log.log(Level.WARNING, "The list of skins could not be downloaded.", e);
			} finally {
				synchronized (RemoteSkinList.this) {
					RemoteSkinList.this.skinProperties = skinsProp;

					log.info(RemoteSkinList.this.skinProperties.size()+" skin(s) enumerated.");
				}
			}

			return RemoteSkinList.this;
		}

		@Override
		public double getProgress() {
			return -1;
		}

		@Override
		public String getStatus() {
			return this.progress.getStatus();
		}
	}
}