/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JProgressBar;

public class WebpageLayoutManager
implements LayoutManager {
    private static final int PROGRESS_WIDTH = 100;

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        throw new UnsupportedOperationException("Can't remove things!");
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
        int numComps = parent.getComponentCount();
        for (int i = 0; i < numComps; ++i) {
            Component comp = parent.getComponent(i);
            if (comp instanceof JProgressBar) {
                Dimension size = comp.getPreferredSize();
                comp.setLocation((parent.getWidth() - 100) / 2, (int)((double)parent.getHeight() / 2.0 - (double)size.height / 2.0));
                comp.setSize(100, comp.getPreferredSize().height);
                continue;
            }
            comp.setLocation(insets.left, insets.top);
            comp.setSize(maxWidth, maxHeight);
        }
    }
}

