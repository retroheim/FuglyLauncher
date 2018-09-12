/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.beust.jcommander.Parameter;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang.StringUtils;

public class LauncherArguments {
    @Parameter(names={"--dir"})
    private File dir;
    @Parameter(names={"--bootstrap-version"})
    private Integer bootstrapVersion;
    @Parameter(names={"--portable"})
    private boolean portable;
    @Parameter(names={"--uripath"})
    private String uriPath;
    @Parameter(names={"--edition"})
    private String edition;
    @Parameter(names={"--run"})
    private String run;
    @Parameter(names={"--key"})
    private String key;

    public void processURI() {
        String uripath = this.getUriPath();
        if (!StringUtils.isEmpty(uripath)) {
            try {
                String scheme;
                URI uri;
                if (StringUtils.startsWith(uripath, "'") && StringUtils.endsWith(uripath, "'")) {
                    uripath = StringUtils.substringBetween(uripath, "'");
                }
                if (StringUtils.isEmpty(scheme = (uri = new URI(uripath)).getScheme()) || StringUtils.equalsIgnoreCase(scheme, "fruitlauncher")) {
                    String query;
                    String[] uripaths;
                    String host = uri.getHost();
                    String path = uri.getPath();
                    if (!StringUtils.isEmpty(path) && StringUtils.equalsIgnoreCase(host, "run") && (uripaths = StringUtils.split(path, "/")).length > 0) {
                        String runid = uripaths[0];
                        this.setRun(runid);
                    }
                    if (!StringUtils.isEmpty(query = uri.getQuery())) {
                        String[] qStrings;
                        for (String qString : qStrings = StringUtils.split(query, "&")) {
                            String key = StringUtils.substringBefore(qString, "=");
                            String value = StringUtils.substringAfter(qString, "=");
                            if (!StringUtils.equalsIgnoreCase(key, "key") || StringUtils.isEmpty(value)) continue;
                            this.setKey(value);
                        }
                    }
                }
            }
            catch (URISyntaxException uri) {
                // empty catch block
            }
        }
    }

    public File getDir() {
        return this.dir;
    }

    public Integer getBootstrapVersion() {
        return this.bootstrapVersion;
    }

    public boolean isPortable() {
        return this.portable;
    }

    public String getUriPath() {
        return this.uriPath;
    }

    public String getEdition() {
        return this.edition;
    }

    public String getRun() {
        return this.run;
    }

    public String getKey() {
        return this.key;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setBootstrapVersion(Integer bootstrapVersion) {
        this.bootstrapVersion = bootstrapVersion;
    }

    public void setPortable(boolean portable) {
        this.portable = portable;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LauncherArguments)) {
            return false;
        }
        LauncherArguments other = (LauncherArguments)o;
        if (!other.canEqual(this)) {
            return false;
        }
        File this$dir = this.getDir();
        File other$dir = other.getDir();
        if (this$dir == null ? other$dir != null : !this$dir.equals(other$dir)) {
            return false;
        }
        Integer this$bootstrapVersion = this.getBootstrapVersion();
        Integer other$bootstrapVersion = other.getBootstrapVersion();
        if (this$bootstrapVersion == null ? other$bootstrapVersion != null : !this$bootstrapVersion.equals(other$bootstrapVersion)) {
            return false;
        }
        if (this.isPortable() != other.isPortable()) {
            return false;
        }
        String this$uriPath = this.getUriPath();
        String other$uriPath = other.getUriPath();
        if (this$uriPath == null ? other$uriPath != null : !this$uriPath.equals(other$uriPath)) {
            return false;
        }
        String this$edition = this.getEdition();
        String other$edition = other.getEdition();
        if (this$edition == null ? other$edition != null : !this$edition.equals(other$edition)) {
            return false;
        }
        String this$run = this.getRun();
        String other$run = other.getRun();
        if (this$run == null ? other$run != null : !this$run.equals(other$run)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        return true;
    }

    public boolean canEqual(Object other) {
        return other instanceof LauncherArguments;
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        File $dir = this.getDir();
        result = result * 31 + ($dir == null ? 0 : $dir.hashCode());
        Integer $bootstrapVersion = this.getBootstrapVersion();
        result = result * 31 + ($bootstrapVersion == null ? 0 : $bootstrapVersion.hashCode());
        result = result * 31 + (this.isPortable() ? 1231 : 1237);
        String $uriPath = this.getUriPath();
        result = result * 31 + ($uriPath == null ? 0 : $uriPath.hashCode());
        String $edition = this.getEdition();
        result = result * 31 + ($edition == null ? 0 : $edition.hashCode());
        String $run = this.getRun();
        result = result * 31 + ($run == null ? 0 : $run.hashCode());
        String $key = this.getKey();
        result = result * 31 + ($key == null ? 0 : $key.hashCode());
        return result;
    }

    public String toString() {
        return "LauncherArguments(dir=" + this.getDir() + ", bootstrapVersion=" + this.getBootstrapVersion() + ", portable=" + this.isPortable() + ", uriPath=" + this.getUriPath() + ", edition=" + this.getEdition() + ", run=" + this.getRun() + ", key=" + this.getKey() + ")";
    }
}

