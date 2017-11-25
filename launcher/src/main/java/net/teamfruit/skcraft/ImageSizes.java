package net.teamfruit.skcraft;

import java.awt.Dimension;

import javax.annotation.Nonnull;

public enum ImageSizes {
	RAW {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			return new Dimension(w, h);
		}
	},
	MAX {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			return new Dimension(maxw, maxh);
		}
	},
	WIDTH {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			return new Dimension(maxw, h*maxw/w);
		}
	},
	HEIGHT {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			return new Dimension(w*maxh/h, maxh);
		}
	},
	INNER {
		@Override
		public @Nonnull Dimension size(final int w, final int h, int maxw, int maxh) {
			if (w<0)
				maxw *= -1;
			if (h<0)
				maxh *= -1;
			final boolean b = w/maxw>h/maxh;
			return new Dimension(b ? maxw : w*maxh/h, b ? h*maxw/w : maxh);
		}
	},
	OUTER {
		@Override
		public @Nonnull Dimension size(final int w, final int h, int maxw, int maxh) {
			if (w<0)
				maxw *= -1;
			if (h<0)
				maxh *= -1;
			final boolean b = w/maxw<h/maxh;
			return new Dimension(b ? maxw : w*maxh/h, b ? h*maxw/w : maxh);
		}
	},
	WIDTH_LIMIT {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			if (w<maxw)
				return new Dimension(w, h);
			else
				return new Dimension(maxw, maxw*h/w);
		}
	},
	HEIGHT_LIMIT {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			if (h<maxh)
				return new Dimension(w, h);
			else
				return new Dimension(maxh*w/h, maxh);
		}
	},
	LIMIT {
		@Override
		public @Nonnull Dimension size(final int w, final int h, final int maxw, final int maxh) {
			if (w>h)
				if (w<maxw)
					return new Dimension(w, h);
				else
					return new Dimension(maxw, maxw*h/w);
			else if (h<maxh)
				return new Dimension(w, h);
			else
				return new Dimension(maxh*w/h, maxh);
		}
	},
	;

	public abstract @Nonnull Dimension size(int w, int h, int maxw, int maxh);
}
