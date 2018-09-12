/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.auth;

import com.skcraft.launcher.auth.AuthenticationException;
import com.skcraft.launcher.auth.Session;
import java.io.IOException;
import java.util.List;

public interface LoginService {
    public List<? extends Session> login(String var1, String var2, String var3) throws IOException, InterruptedException, AuthenticationException;
}

