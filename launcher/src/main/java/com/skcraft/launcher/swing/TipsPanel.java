package com.skcraft.launcher.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.skcraft.launcher.Launcher;

import lombok.Getter;
import net.teamfruit.skcraft.ImageSizes;
import net.teamfruit.skcraft.SizeData;

public class TipsPanel extends JPanel {
	public static class DefaultIcons {
		public static final Image tipsIcon = SwingHelper.createImage(Launcher.class, "tips_icon.png");
	}

	private JLabel pic = new JLabel();
	private Timer tm;
	private int x = -1;
	private Random rnd = new Random();
	private boolean random = true;
	private Image thumb;
	private String title = "SUSHI";

	//Images Path In Array
	private final @Getter List<Image> list = new ArrayList<Image>();
	{
		for (final String link: new String[] {
				"https://b.ppy.sh/thumb/133771.jpg", //0
				"https://b.ppy.sh/thumb/165991.jpg", //1
				"https://b.ppy.sh/thumb/128604.jpg", //2
				"https://b.ppy.sh/thumb/155114.jpg", //3
				"https://b.ppy.sh/thumb/136241.jpg", //4
		})
			this.list.add(SwingHelper.createImage(link));
	}

	public TipsPanel() {
		this.pic.setHorizontalAlignment(SwingConstants.CENTER);
		this.pic.setVerticalAlignment(SwingConstants.CENTER);

		//set a timer
		this.tm = new Timer(4*1000, new ActionListener() {
			{
				next();
			}

			@Override
			public void actionPerformed(final ActionEvent e) {
				next();
			}

			private void next() {
				if (TipsPanel.this.random) {
					TipsPanel.this.x = TipsPanel.this.rnd.nextInt(TipsPanel.this.list.size());
					TipsPanel.this.thumb = TipsPanel.this.list.get(TipsPanel.this.x);
					repaint();
				} else {
					if (++TipsPanel.this.x<0||TipsPanel.this.x>=TipsPanel.this.list.size())
						TipsPanel.this.x = 0;
					TipsPanel.this.thumb = TipsPanel.this.list.get(TipsPanel.this.x);
					repaint();
				}
			}
		});

		setLayout(new GridBagLayout());
		add(this.pic);

		this.tm.start();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		final int panel_width = getWidth();
		final int panel_height = getHeight();
		if (this.thumb!=null) {
			final int img_width = this.thumb.getWidth(this);
			final int img_height = this.thumb.getHeight(this);
			final SizeData img_size = ImageSizes.OUTER.size(img_width, img_height, panel_width, panel_height);
			g2d.drawImage(this.thumb, (int)((panel_width-img_size.getWidth())/2), (int)((panel_height-img_size.getHeight())/2), (int)img_size.getWidth(), (int)img_size.getHeight(), this);
		}
		if (this.title!=null) {
			final Font font = new Font(Font.DIALOG, Font.BOLD, 14);
			g2d.setFont(font);
			final FontMetrics fontmatrics = g2d.getFontMetrics();
			g2d.translate(0, -5);
			final int height_padding = 5;
			final int width_padding_right = 34;
			final int width_padding_left = 24;
			final int pol_w = fontmatrics.stringWidth(this.title)+width_padding_right;
			final int pol_h = fontmatrics.getHeight()+height_padding;

			final Image titleicon = DefaultIcons.tipsIcon;
			final int title_width = titleicon.getWidth(this);
			final int title_height = titleicon.getHeight(this);

			final int title_newwidth = pol_h*title_width/title_height;
			final int title_newheight = pol_h;
			g2d.drawImage(titleicon, panel_width-pol_w, panel_height-title_newheight, title_newwidth, title_newheight, this);

			g2d.setColor(Color.WHITE);
			g2d.drawString(this.title, panel_width-pol_w+width_padding_left, panel_height-fontmatrics.getDescent()-height_padding/2);
			g2d.translate(0, 5);
		}
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("");
		final TipsPanel panel = new TipsPanel();
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(250, 250);
		frame.setVisible(true);
	}
}