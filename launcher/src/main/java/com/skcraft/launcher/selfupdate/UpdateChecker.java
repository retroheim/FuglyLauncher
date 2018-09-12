/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.selfupdate;

import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.LauncherException;
import com.skcraft.launcher.selfupdate.ComparableVersion;
import com.skcraft.launcher.selfupdate.LatestVersionInfo;
import com.skcraft.launcher.util.HttpRequest;
import com.skcraft.launcher.util.SharedLocale;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import lombok.NonNull;

public class UpdateChecker
implements Callable<URL> {
    private static final Logger log = Logger.getLogger(UpdateChecker.class.getName());
    private final Launcher launcher;

    public UpdateChecker(@NonNull Launcher launcher) {
        if (launcher == null) {
            throw new NullPointerException("launcher");
        }
        this.launcher = launcher;
    }

    @Override
    public URL call() throws Exception {
        try {
            log.info("Checking for update...");
            URL url = HttpRequest.url(this.launcher.getProperties().getProperty("selfUpdateUrl"));
            LatestVersionInfo versionInfo = HttpRequest.get(url).execute().expectResponseCode(200).returnContent().asJson(LatestVersionInfo.class);
            ComparableVersion current = new ComparableVersion(this.launcher.getVersion());
            ComparableVersion latest = new ComparableVersion(versionInfo.getVersion());
            log.info("Latest version is " + latest + ", while current is " + current);
            if (latest.compareTo(current) >= 1) {
                log.info("Update available at " + versionInfo.getUrl());
                return versionInfo.getUrl();
            }
            log.info("No update required.");
            return null;
        }
        catch (Exception e) {
            throw new LauncherException(e, SharedLocale.tr("errors.selfUpdateCheckError"));
        }
    }
}

