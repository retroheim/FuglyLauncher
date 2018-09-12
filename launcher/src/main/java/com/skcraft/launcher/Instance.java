/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.Files;
import com.skcraft.launcher.launch.JavaProcessBuilder;
import com.skcraft.launcher.model.modpack.LaunchModifier;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import net.teamfruit.skcraft.launcher.model.modpack.ConnectServerInfo;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Instance
implements Comparable<Instance> {
    private String title;
    private String thumb;
    private String name;
    private String version;
    private String key;
    private ConnectServerInfo server;
    private boolean updatePending;
    private boolean installed;
    private Date lastAccessed;
    @JsonProperty(value="launch")
    private LaunchModifier launchModifier;
    @JsonIgnore
    private File dir;
    @JsonIgnore
    private URL manifestURL;
    @JsonIgnore
    private int priority;
    @JsonIgnore
    private boolean selected;
    @JsonIgnore
    private boolean local;

    public String getTitle() {
        return this.title != null ? this.title : this.name;
    }

    public void modify(JavaProcessBuilder builder) {
        if (this.launchModifier != null) {
            this.launchModifier.modify(builder);
        }
    }

    public File getDir() {
        try {
            Files.createParentDirs(this.dir);
            this.dir.mkdir();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this.dir;
    }

    @JsonIgnore
    public File getContentDir() {
        File dir = new File(this.dir, "minecraft");
        try {
            Files.createParentDirs(dir);
            dir.mkdir();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return dir;
    }

    @JsonIgnore
    public File getManifestPath() {
        return new File(this.getDir(), "manifest.json");
    }

    @JsonIgnore
    public File getVersionPath() {
        return new File(this.getDir(), "version.json");
    }

    @JsonIgnore
    public File getCustomJarPath() {
        return new File(this.getContentDir(), "custom_jar.jar");
    }

    public String toString() {
        return this.name;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int compareTo(Instance o) {
        if (this.isLocal() && !o.isLocal()) {
            return -1;
        }
        if (!this.isLocal() && o.isLocal()) {
            return 1;
        }
        if (this.isLocal() && o.isLocal()) {
            Date otherDate = o.getLastAccessed();
            if (otherDate == null && this.lastAccessed == null) {
                return 0;
            }
            if (otherDate == null) {
                return -1;
            }
            if (this.lastAccessed == null) {
                return 1;
            }
            return - this.lastAccessed.compareTo(otherDate);
        }
        if (this.priority > o.priority) {
            return -1;
        }
        if (this.priority < o.priority) {
            return 1;
        }
        return 0;
    }

    public String getThumb() {
        return this.thumb;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getKey() {
        return this.key;
    }

    public ConnectServerInfo getServer() {
        return this.server;
    }

    public boolean isUpdatePending() {
        return this.updatePending;
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public Date getLastAccessed() {
        return this.lastAccessed;
    }

    public LaunchModifier getLaunchModifier() {
        return this.launchModifier;
    }

    public URL getManifestURL() {
        return this.manifestURL;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setServer(ConnectServerInfo server) {
        this.server = server;
    }

    public void setUpdatePending(boolean updatePending) {
        this.updatePending = updatePending;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public void setLaunchModifier(LaunchModifier launchModifier) {
        this.launchModifier = launchModifier;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setManifestURL(URL manifestURL) {
        this.manifestURL = manifestURL;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }
}

