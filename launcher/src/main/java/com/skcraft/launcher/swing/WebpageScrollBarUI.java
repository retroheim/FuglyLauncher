package com.skcraft.launcher.swing;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class WebpageScrollBarUI extends BasicScrollBarUI {
	private static final int SCROLL_BAR_ALPHA_ROLLOVER = 100;
	private static final int SCROLL_BAR_ALPHA = 50;
	private static final int THUMB_SIZE = 8;
	private static final Color THUMB_COLOR = Color.BLACK;

	private JScrollPane sp;

	public WebpageScrollBarUI(final JScrollPane sp) {
		this.sp = sp;
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return this.scrollbar.getOrientation()==Adjustable.VERTICAL
				? new Dimension(THUMB_SIZE, 48)
				: new Dimension(48, THUMB_SIZE);
	}

	@Override
	protected JButton createDecreaseButton(final int orientation) {
		return new InvisibleScrollBarButton();
	}

	@Override
	protected JButton createIncreaseButton(final int orientation) {
		return new InvisibleScrollBarButton();
	}

	@Override
	protected void paintTrack(final Graphics g, final JComponent c, final Rectangle trackBounds) {
	}

	@Override
	protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
		final int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
		final int orientation = this.scrollbar.getOrientation();
		final int x = thumbBounds.x;
		final int y = thumbBounds.y;

		int width = orientation==Adjustable.VERTICAL ? THUMB_SIZE : thumbBounds.width;
		width = Math.max(width, THUMB_SIZE);

		int height = orientation==Adjustable.VERTICAL ? thumbBounds.height : THUMB_SIZE;
		height = Math.max(height, THUMB_SIZE);

		final Graphics2D graphics2D = (Graphics2D) g.create();
		graphics2D.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
		graphics2D.fillRect(x, y, width, height);
		graphics2D.dispose();
	}

	@Override
	protected void setThumbBounds(final int x, final int y, final int width, final int height) {
		super.setThumbBounds(x, y, width, height);
		this.sp.repaint();
	}

	/**
	 * Invisible Buttons, to hide scroll bar buttons
	 */
	private static class InvisibleScrollBarButton extends JButton {

		private static final long serialVersionUID = 1552427919226628689L;

		private InvisibleScrollBarButton() {
			setOpaque(false);
			setFocusable(false);
			setFocusPainted(false);
			setBorderPainted(false);
			setBorder(BorderFactory.createEmptyBorder());
		}
	}
}
