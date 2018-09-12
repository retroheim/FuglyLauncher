/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher;

import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.dialog.LauncherFrame;
import com.skcraft.launcher.swing.InstanceTable;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.swing.WebpagePanel;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import lombok.NonNull;

public class FancyLauncherFrame
extends LauncherFrame {
    public FancyLauncherFrame(@NonNull Launcher launcher) {
        super(launcher);
        if (launcher == null) {
            throw new NullPointerException("launcher");
        }
        this.setLocationRelativeTo(null);
        SwingHelper.removeOpaqueness(this.getInstancesTable());
        SwingHelper.removeOpaqueness(this.getInstanceScroll());
        this.getInstanceScroll().setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected WebpagePanel createNewsPanel() {
        WebpagePanel panel = super.createNewsPanel();
        panel.setBrowserBorder(BorderFactory.createEmptyBorder());
        return panel;
    }
}

