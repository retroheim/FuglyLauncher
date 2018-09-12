/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher;

import com.google.common.io.Files;
import com.skcraft.concurrency.ProgressObservable;
import com.skcraft.launcher.LauncherException;
import com.skcraft.launcher.model.minecraft.Asset;
import com.skcraft.launcher.model.minecraft.AssetsIndex;
import com.skcraft.launcher.model.minecraft.VersionManifest;
import com.skcraft.launcher.persistence.Persistence;
import com.skcraft.launcher.util.SharedLocale;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;

public class AssetsRoot {
    private static final Logger log = Logger.getLogger(AssetsRoot.class.getName());
    private final File dir;

    public AssetsRoot(@NonNull File dir) {
        if (dir == null) {
            throw new NullPointerException("dir");
        }
        this.dir = dir;
    }

    public File getIndexPath(VersionManifest versionManifest) {
        return new File(this.dir, "indexes/" + versionManifest.getAssetsIndex() + ".json");
    }

    public File getObjectPath(Asset asset) {
        String hash = asset.getHash();
        return new File(this.dir, "objects/" + hash.substring(0, 2) + "/" + hash);
    }

    public AssetsTreeBuilder createAssetsBuilder(@NonNull VersionManifest versionManifest) throws LauncherException {
        if (versionManifest == null) {
            throw new NullPointerException("versionManifest");
        }
        String indexId = versionManifest.getAssetsIndex();
        File path = this.getIndexPath(versionManifest);
        AssetsIndex index = Persistence.read(path, AssetsIndex.class, true);
        if (index == null || index.getObjects() == null) {
            throw new LauncherException("Missing index at " + path, SharedLocale.tr("assets.missingIndex", path.getAbsolutePath()));
        }
        File treeDir = new File(this.dir, "virtual/" + indexId);
        treeDir.mkdirs();
        return new AssetsTreeBuilder(index, treeDir);
    }

    public File getDir() {
        return this.dir;
    }

    public class AssetsTreeBuilder
    implements ProgressObservable {
        private final AssetsIndex index;
        private final File destDir;
        private final int count;
        private int processed = 0;

        public AssetsTreeBuilder(AssetsIndex index, File destDir) {
            this.index = index;
            this.destDir = destDir;
            this.count = index.getObjects().size();
        }

        public File build() throws IOException, LauncherException {
            log.info("Building asset virtual tree at '" + this.destDir.getAbsolutePath() + "'...");
            for (Map.Entry<String, Asset> entry : this.index.getObjects().entrySet()) {
                File objectPath = AssetsRoot.this.getObjectPath(entry.getValue());
                File virtualPath = new File(this.destDir, entry.getKey());
                virtualPath.getParentFile().mkdirs();
                if (!virtualPath.exists()) {
                    log.log(Level.INFO, "Copying {0} to {1}...", new Object[]{objectPath.getAbsolutePath(), virtualPath.getAbsolutePath()});
                    if (!objectPath.exists()) {
                        String message = SharedLocale.tr("assets.missingObject", objectPath.getAbsolutePath());
                        throw new LauncherException("Missing object " + objectPath.getAbsolutePath(), message);
                    }
                    Files.copy(objectPath, virtualPath);
                }
                ++this.processed;
            }
            return this.destDir;
        }

        @Override
        public double getProgress() {
            if (this.count == 0) {
                return -1.0;
            }
            return (double)this.processed / (double)this.count;
        }

        @Override
        public String getStatus() {
            if (this.count == 0) {
                return SharedLocale.tr("assets.expanding1", this.count, this.count - this.processed);
            }
            return SharedLocale.tr("assets.expandingN", this.count, this.count - this.processed);
        }
    }

}

