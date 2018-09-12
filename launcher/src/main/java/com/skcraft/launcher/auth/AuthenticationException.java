/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.auth;

import com.skcraft.launcher.LauncherException;

public class AuthenticationException
extends LauncherException {
    public AuthenticationException(String message, String localizedMessage) {
        super(message, localizedMessage);
    }

    public AuthenticationException(Throwable cause, String localizedMessage) {
        super(cause, localizedMessage);
    }
}

