package com.skcraft.launcher.swing;

import java.awt.Component;
import java.awt.Image;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.skcraft.launcher.Instance;
import com.skcraft.launcher.Launcher;

public class InstanceTableCellFactory implements TableCellRenderer {
	private static class DefaultIcons {
		public static final Image loadingIcon = SwingHelper.createImage(Launcher.class, "loading_icon.png");
		public static final Image instanceIcon = SwingHelper.createImage(Launcher.class, "instance_icon.png");
		public static final Image customInstanceIcon = SwingHelper.createImage(Launcher.class, "custom_instance_icon.png");
		public static final Image downloadIcon = SwingHelper.createImage(Launcher.class, "download_icon.png");
	}

	private static final @Nonnull ExecutorService threadpool = new ThreadPoolExecutor(3, 3,
			4L, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(),
			new ThreadFactoryBuilder().setNameFormat("thumbnail-download-%d").build());

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		final InstanceTableCellPanel tablecell = new InstanceTableCellPanel(table);

		if (value instanceof Instance) {
			final Instance instance = (Instance) value;
			tablecell.setTitle(instance.getTitle());

			if (!instance.isLocal())
				tablecell.setThumb(DefaultIcons.downloadIcon);
			else if (instance.getThumb()!=null) {
				if (instance.getIconCache()!=null)
					tablecell.setThumb(instance.getIconCache());
				else {
					tablecell.setThumb(DefaultIcons.loadingIcon);
					threadpool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								final Image thumb = SwingHelper.createImage("https://i.gyazo.com/18740a42fbce0032a095b7945f1eef86.png");
								tablecell.setThumb(thumb);
								instance.setIconCache(thumb);
							} catch (final Exception e) {
								tablecell.setThumb(DefaultIcons.instanceIcon);
							}
							table.repaint();
						}
					});
				}
			} else if (instance.getManifestURL()!=null)
				tablecell.setThumb(DefaultIcons.instanceIcon);
			else
				tablecell.setThumb(DefaultIcons.customInstanceIcon);
		}

		return tablecell;
	}
}
