/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.selfupdate;

import com.skcraft.concurrency.DefaultProgress;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.install.Downloader;
import com.skcraft.launcher.install.FileMover;
import com.skcraft.launcher.install.InstallTask;
import com.skcraft.launcher.install.Installer;
import com.skcraft.launcher.util.SharedLocale;
import java.io.File;
import java.net.URL;
import java.util.concurrent.Callable;
import lombok.NonNull;

public class SelfUpdater
implements Callable<File>,
ProgressObservable {
    private final Launcher launcher;
    private final URL url;
    private final Installer installer;
    private ProgressObservable progress = new DefaultProgress(0.0, SharedLocale.tr("updater.updating"));

    public SelfUpdater(@NonNull Launcher launcher, @NonNull URL url) {
        if (launcher == null) {
            throw new NullPointerException("launcher");
        }
        if (url == null) {
            throw new NullPointerException("url");
        }
        this.launcher = launcher;
        this.url = url;
        this.installer = new Installer(launcher.getInstallerDir());
    }

    @Override
    public File call() throws Exception {
        File dir = this.launcher.getLauncherBinariesDir();
        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jar.pack");
        File tempFile = this.installer.getDownloader().download(this.url, "", 10000L, "launcher.jar.pack");
        this.progress = this.installer.getDownloader();
        this.installer.download();
        this.installer.queue(new FileMover(tempFile, file));
        this.progress = this.installer;
        this.installer.execute();
        return file;
    }

    @Override
    public double getProgress() {
        return this.progress.getProgress();
    }

    @Override
    public String getStatus() {
        return this.progress.getStatus();
    }
}

