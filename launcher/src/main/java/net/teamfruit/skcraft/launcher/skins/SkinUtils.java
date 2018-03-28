package net.teamfruit.skcraft.launcher.skins;

import java.awt.Window;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.skcraft.concurrency.ObservableFuture;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.dialog.ProgressDialog;
import com.skcraft.launcher.util.SharedLocale;
import com.skcraft.launcher.util.SwingExecutor;

public class SkinUtils {
	public static void loadSkinList(final Window window, final Launcher launcher, final Predicate<RemoteSkinList> callback) {
		RemoteSkinList.Enumerator remoteSkinListEnumerator = launcher.getRemoteSkins().createEnumerator();
		ObservableFuture<RemoteSkinList> skinListFuture = new ObservableFuture<RemoteSkinList>(launcher.getExecutor().submit(remoteSkinListEnumerator), remoteSkinListEnumerator);
		Futures.addCallback(skinListFuture, new FutureCallback<RemoteSkinList>() {
			@Override
			public void onSuccess(RemoteSkinList remoteSkinList) {
				callback.apply(remoteSkinList);
			}

			@Override
			public void onFailure(Throwable t) {
			}
		}, SwingExecutor.INSTANCE);
		ProgressDialog.showProgress(window, skinListFuture, SharedLocale.tr("skins.loadingListTitle"), SharedLocale.tr("skins.loadingList"));
	}

	public static void loadSkin(final Window window, final Launcher launcher, final Predicate<RemoteSkin> callback, RemoteSkin remoteSkin) {
		if (remoteSkin!=null) {
			RemoteSkin.Enumerator remoteSkinEnumerator = remoteSkin.createEnumerator();
			ObservableFuture<RemoteSkin> skinFuture = new ObservableFuture<RemoteSkin>(launcher.getExecutor().submit(remoteSkinEnumerator), remoteSkinEnumerator);
			Futures.addCallback(skinFuture, new FutureCallback<RemoteSkin>() {
				@Override
				public void onSuccess(RemoteSkin remoteSkin) {
					callback.apply(remoteSkin);
				}

				@Override
				public void onFailure(Throwable t) {
				}
			});
			ProgressDialog.showProgress(window, skinFuture, SharedLocale.tr("skins.loadingTitle"), SharedLocale.tr("skins.loading"));
		}
	}
}
