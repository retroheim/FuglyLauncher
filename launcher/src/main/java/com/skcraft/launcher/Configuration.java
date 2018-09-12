/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Configuration {
    private String skin;
    private boolean offlineEnabled = false;
    private boolean offlineModeEnabled = false;
    private String offlineModePlayerName;
    private String jvmPath;
    private String jvmArgs;
    private int minMemory = 1024;
    private int maxMemory = 0;
    private int permGen = 256;
    private boolean showConsole = true;
    private int windowWidth = 854;
    private int widowHeight = 480;
    private boolean proxyEnabled = false;
    private String proxyHost = "localhost";
    private int proxyPort = 8080;
    private String proxyUsername;
    private String proxyPassword;
    private String gameKey;
    private boolean serverEnabled = false;
    private String serverHost;
    private int serverPort = 25565;
    private String pathCommonDataDir = "";
    private String pathInstancesDir = "instances";

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String getSkin() {
        return this.skin;
    }

    public boolean isOfflineEnabled() {
        return this.offlineEnabled;
    }

    public boolean isOfflineModeEnabled() {
        return this.offlineModeEnabled;
    }

    public String getOfflineModePlayerName() {
        return this.offlineModePlayerName;
    }

    public String getJvmPath() {
        return this.jvmPath;
    }

    public String getJvmArgs() {
        return this.jvmArgs;
    }

    public int getMinMemory() {
        return this.minMemory;
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    public int getPermGen() {
        return this.permGen;
    }

    public boolean isShowConsole() {
        return this.showConsole;
    }

    public int getWindowWidth() {
        return this.windowWidth;
    }

    public int getWidowHeight() {
        return this.widowHeight;
    }

    public boolean isProxyEnabled() {
        return this.proxyEnabled;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public String getProxyUsername() {
        return this.proxyUsername;
    }

    public String getProxyPassword() {
        return this.proxyPassword;
    }

    public String getGameKey() {
        return this.gameKey;
    }

    public boolean isServerEnabled() {
        return this.serverEnabled;
    }

    public String getServerHost() {
        return this.serverHost;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public String getPathCommonDataDir() {
        return this.pathCommonDataDir;
    }

    public String getPathInstancesDir() {
        return this.pathInstancesDir;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public void setOfflineEnabled(boolean offlineEnabled) {
        this.offlineEnabled = offlineEnabled;
    }

    public void setOfflineModeEnabled(boolean offlineModeEnabled) {
        this.offlineModeEnabled = offlineModeEnabled;
    }

    public void setOfflineModePlayerName(String offlineModePlayerName) {
        this.offlineModePlayerName = offlineModePlayerName;
    }

    public void setJvmPath(String jvmPath) {
        this.jvmPath = jvmPath;
    }

    public void setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public void setMinMemory(int minMemory) {
        this.minMemory = minMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    public void setPermGen(int permGen) {
        this.permGen = permGen;
    }

    public void setShowConsole(boolean showConsole) {
        this.showConsole = showConsole;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWidowHeight(int widowHeight) {
        this.widowHeight = widowHeight;
    }

    public void setProxyEnabled(boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public void setServerEnabled(boolean serverEnabled) {
        this.serverEnabled = serverEnabled;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setPathCommonDataDir(String pathCommonDataDir) {
        this.pathCommonDataDir = pathCommonDataDir;
    }

    public void setPathInstancesDir(String pathInstancesDir) {
        this.pathInstancesDir = pathInstancesDir;
    }

    public String toString() {
        return "Configuration(skin=" + this.getSkin() + ", offlineEnabled=" + this.isOfflineEnabled() + ", offlineModeEnabled=" + this.isOfflineModeEnabled() + ", offlineModePlayerName=" + this.getOfflineModePlayerName() + ", jvmPath=" + this.getJvmPath() + ", jvmArgs=" + this.getJvmArgs() + ", minMemory=" + this.getMinMemory() + ", maxMemory=" + this.getMaxMemory() + ", permGen=" + this.getPermGen() + ", showConsole=" + this.isShowConsole() + ", windowWidth=" + this.getWindowWidth() + ", widowHeight=" + this.getWidowHeight() + ", proxyEnabled=" + this.isProxyEnabled() + ", proxyHost=" + this.getProxyHost() + ", proxyPort=" + this.getProxyPort() + ", proxyUsername=" + this.getProxyUsername() + ", proxyPassword=" + this.getProxyPassword() + ", gameKey=" + this.getGameKey() + ", serverEnabled=" + this.isServerEnabled() + ", serverHost=" + this.getServerHost() + ", serverPort=" + this.getServerPort() + ", pathCommonDataDir=" + this.getPathCommonDataDir() + ", pathInstancesDir=" + this.getPathInstancesDir() + ")";
    }
}

