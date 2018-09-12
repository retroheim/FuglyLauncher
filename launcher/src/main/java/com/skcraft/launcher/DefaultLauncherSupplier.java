/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.google.common.base.Supplier;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.dialog.LauncherFrame;
import java.awt.Window;

public class DefaultLauncherSupplier
implements Supplier<Window> {
    private final Launcher launcher;

    public DefaultLauncherSupplier(Launcher launcher) {
        this.launcher = launcher;
    }

    @Override
    public Window get() {
        return new LauncherFrame(this.launcher);
    }
}

