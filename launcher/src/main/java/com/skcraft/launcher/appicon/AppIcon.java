package com.skcraft.launcher.appicon;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.skcraft.launcher.swing.SwingHelper;

import lombok.Getter;

public class AppIcon {
    public static void setFrameIconSet(JFrame frame, AppIconSet iconSet) {
        frame.setIconImages(iconSet.getIcons());
    }

    public static class AppIconSet {
    	private final @Getter List<Image> icons;

    	public AppIconSet(List<Image> icons) {
			this.icons = icons;
		}

    	public Image getIcon() {
    		if (!icons.isEmpty())
    			return icons.get(0);
			return null;
    	}

    	public static AppIconSet getIconSet(Class<?> clazz, String... paths) {
    		List<Image> icons = new ArrayList<Image>();
    		for (String path:paths) {
    			BufferedImage image = SwingHelper.readBufferedImage(clazz, path);
    	        if (image != null)
					icons.add(image);
    		}
    		return new AppIconSet(icons);
    	}

    	public static AppIconSet getIconSetFromDirWithFormat(Class<?> clazz, String path_format) {
    		String[] icon_paths = {
    				String.format(path_format, "16x16"),
    				String.format(path_format, "32x32"),
    				String.format(path_format, "64x64"),
    				String.format(path_format, "128x128"),
    		};
    		return getIconSet(clazz, icon_paths);
    	}
    }
}
