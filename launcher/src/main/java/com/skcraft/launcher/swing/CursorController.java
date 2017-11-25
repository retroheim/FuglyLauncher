package com.skcraft.launcher.swing;

import javax.swing.JComponent;

import lombok.Getter;

public class CursorController {
	private @Getter JComponent controlee;

	public CursorController(final JComponent controlee) {
		this.controlee = controlee;
	}


}
