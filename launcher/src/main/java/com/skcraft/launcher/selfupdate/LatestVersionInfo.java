/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.selfupdate;

import java.net.URL;

public class LatestVersionInfo {
    private String version;
    private URL url;

    public String getVersion() {
        return this.version;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LatestVersionInfo)) {
            return false;
        }
        LatestVersionInfo other = (LatestVersionInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$version = this.getVersion();
        String other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) {
            return false;
        }
        URL this$url = this.getUrl();
        URL other$url = other.getUrl();
        if (this$url == null ? other$url != null : !this$url.equals(other$url)) {
            return false;
        }
        return true;
    }

    public boolean canEqual(Object other) {
        return other instanceof LatestVersionInfo;
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        String $version = this.getVersion();
        result = result * 31 + ($version == null ? 0 : $version.hashCode());
        URL $url = this.getUrl();
        result = result * 31 + ($url == null ? 0 : $url.hashCode());
        return result;
    }

    public String toString() {
        return "LatestVersionInfo(version=" + this.getVersion() + ", url=" + this.getUrl() + ")";
    }
}

