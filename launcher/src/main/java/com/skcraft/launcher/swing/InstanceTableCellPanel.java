package com.skcraft.launcher.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JTable;

import lombok.Getter;
import lombok.Setter;

public class InstanceTableCellPanel extends JPanel {
	private final JTable table;
	private @Getter @Setter String title;
	private @Getter @Setter Image thumb;

	public InstanceTableCellPanel(final JTable table) {
		SwingHelper.removeOpaqueness(this);
		this.table = table;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final int panel_width = getWidth();
		final int panel_height = getHeight();
		if (this.thumb!=null) {
			final int img_width = this.thumb.getWidth(this.table);
			final int img_height = this.thumb.getHeight(this.table);
			final int newwidth = panel_height*img_width/img_height;
			g2d.drawImage(this.thumb, (panel_width-newwidth)/2, 0, newwidth, panel_height, this.table);
		}
		if (this.title!=null) {
			final Font font = new Font(Font.DIALOG, Font.BOLD, 13);
			g2d.setFont(font);
			final FontMetrics fontmatrics = g2d.getFontMetrics();
			g2d.translate(0, -10);
			final Polygon polygon = new Polygon();
			final int pol_w = fontmatrics.stringWidth(this.title)+30;
			final int pol_h = fontmatrics.getHeight()+30;
			final int pol_delta = 10;
			polygon.addPoint(panel_width, panel_height);
			polygon.addPoint(panel_width-pol_w-pol_delta, panel_height);
			polygon.addPoint(panel_width-pol_w, panel_height-pol_h);
			polygon.addPoint(panel_width, panel_height-pol_h);
			g2d.setColor(new Color(0f, 0f, 0f, 0.75f));
			g2d.fillPolygon(polygon);
			g2d.setColor(Color.WHITE);
			g2d.drawString(this.title, panel_width-pol_w+15, panel_height-fontmatrics.getDescent()-15);
		}
	}
}
