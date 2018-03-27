package net.teamfruit.skcraft.launcher.skins;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.skcraft.concurrency.DefaultProgress;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.util.HttpRequest;
import com.skcraft.launcher.util.SharedLocale;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.model.skins.SkinInfo;

@Log
@RequiredArgsConstructor
public class RemoteSkin {

	private final Launcher launcher;
	private final String name;
	private final String url;
	private LocalSkin localSkin;

	/**
     * Get the remote URL.
     *
     * @return the remote URL
     */
    public URL getRemoteURL() {
        try {
            return HttpRequest.url(
                    String.format(url,
                            URLEncoder.encode(launcher.getVersion(), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
	}

	/**
	 * Get the local skin
	 *
	 * @return the local skin list
	 */
	public LocalSkin getLocalSkin() {
		return localSkin;
	}

	/**
	 * Create a worker that loads the skin from the remote skin.
	 *
	 * @return the worker
	 */
	public Enumerator createEnumerator() {
		return new Enumerator();
	}

	public final class Enumerator implements Callable<RemoteSkin>, ProgressObservable {
		private ProgressObservable progress = new DefaultProgress(-1, null);

		private Enumerator() {
		}

		@Override
		public RemoteSkin call() throws Exception {
			log.info("Enumerating remote skin...");

			LocalSkin localSkin = new LocalSkin(launcher, name);

			this.progress = new DefaultProgress(0.3, SharedLocale.tr("skinLoader.checkingRemote"));

			try {
				final URL skinsURL = RemoteSkin.this.launcher.getSkinsURL();

				localSkin.getDir().mkdirs();
				localSkin.setSkinInfo(HttpRequest
						.get(skinsURL)
						.execute()
						.expectResponseCode(200)
						.returnContent()
						.saveContent(localSkin.getFile())
						.asJson(SkinInfo.class));

				localSkin.getSkin().downloadResources();

			} catch (final IOException e) {
				log.log(Level.WARNING, "The skin could not be downloaded.", e);
			} finally {
				synchronized (RemoteSkin.this) {
					RemoteSkin.this.localSkin = localSkin;

					log.info("the skin "+name+" is downloaded.");
				}
			}

			return RemoteSkin.this;
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