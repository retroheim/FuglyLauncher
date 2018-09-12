/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

public class LauncherException
extends Exception {
    private final String localizedMessage;

    public LauncherException(String message, String localizedMessage) {
        super(message);
        this.localizedMessage = localizedMessage;
    }

    public LauncherException(Throwable cause, String localizedMessage) {
        super(cause.getMessage(), cause);
        this.localizedMessage = localizedMessage;
    }

    @Override
    public String getLocalizedMessage() {
        return this.localizedMessage;
    }
}

