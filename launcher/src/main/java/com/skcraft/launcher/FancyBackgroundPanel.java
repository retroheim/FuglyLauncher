/*
 * SKCraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FancyBackgroundPanel extends JPanel {

	private final Launcher launcher;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image background = launcher.getSkin().getBackgroundImage();
		if (background!=null) {
			g.drawImage(background, 0, 0, null);
		}
	}

}
