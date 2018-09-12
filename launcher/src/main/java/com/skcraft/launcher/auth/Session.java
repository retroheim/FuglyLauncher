/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.auth;

import com.skcraft.launcher.auth.UserType;
import java.util.Map;

public interface Session {
    public String getUuid();

    public String getName();

    public String getClientToken();

    public String getAccessToken();

    public Map<String, String> getUserProperties();

    public String getSessionToken();

    public UserType getUserType();

    public boolean isOnline();
}

