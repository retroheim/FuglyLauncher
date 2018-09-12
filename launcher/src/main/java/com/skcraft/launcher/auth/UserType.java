/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.auth;

public enum UserType {
    LEGACY,
    MOJANG;
    

    private UserType() {
    }

    public String getName() {
        return this.name().toLowerCase();
    }
}

