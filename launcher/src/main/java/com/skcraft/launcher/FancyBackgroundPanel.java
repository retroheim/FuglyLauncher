/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher;

import com.skcraft.launcher.Launcher;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.beans.ConstructorProperties;
import javax.swing.JPanel;
import net.teamfruit.skcraft.launcher.skins.Skin;
import net.teamfruit.skcraft.launcher.util.ImageSizes;
import net.teamfruit.skcraft.launcher.util.SizeData;

public class FancyBackgroundPanel
extends JPanel {
    private final Launcher launcher;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image background = this.launcher.getSkin().getBackgroundImage();
        if (background != null) {
            int panel_width = this.getWidth();
            int panel_height = this.getHeight();
            int img_width = background.getWidth(this);
            int img_height = background.getHeight(this);
            SizeData img_size = ImageSizes.OUTER.size(img_width, img_height, panel_width, panel_height);
            g.drawImage(background, (int)(((float)panel_width - img_size.getWidth()) / 2.0f), (int)(((float)panel_height - img_size.getHeight()) / 2.0f), (int)img_size.getWidth(), (int)img_size.getHeight(), this);
        }
    }

    @ConstructorProperties(value={"launcher"})
    public FancyBackgroundPanel(Launcher launcher) {
        this.launcher = launcher;
    }
}

