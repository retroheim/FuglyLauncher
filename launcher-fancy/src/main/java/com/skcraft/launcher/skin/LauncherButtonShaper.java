/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.skin;

import java.awt.Dimension;
import javax.swing.AbstractButton;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public class LauncherButtonShaper
extends ClassicButtonShaper {
    @Override
    public Dimension getPreferredSize(AbstractButton button, Dimension uiPreferredSize) {
        Dimension size = super.getPreferredSize(button, uiPreferredSize);
        return new Dimension(size.width + 5, size.height + 4);
    }
}

