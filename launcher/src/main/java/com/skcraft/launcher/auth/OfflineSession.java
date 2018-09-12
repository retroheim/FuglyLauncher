/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.auth;

import com.skcraft.launcher.auth.Session;
import com.skcraft.launcher.auth.UserType;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

public class OfflineSession
implements Session {
    private static Map<String, String> dummyProperties = Collections.emptyMap();
    private final String name;

    public OfflineSession(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
    }

    @Override
    public String getUuid() {
        return new UUID(0L, 0L).toString();
    }

    @Override
    public String getClientToken() {
        return "0";
    }

    @Override
    public String getAccessToken() {
        return "0";
    }

    @Override
    public Map<String, String> getUserProperties() {
        return dummyProperties;
    }

    @Override
    public String getSessionToken() {
        return "-";
    }

    @Override
    public UserType getUserType() {
        return UserType.LEGACY;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

