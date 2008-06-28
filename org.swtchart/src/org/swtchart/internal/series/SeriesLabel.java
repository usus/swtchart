package org.swtchart.internal.series;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Constants;
import org.swtchart.ISeriesLabel;

/**
 * A series label.
 */
public class SeriesLabel implements ISeriesLabel {

	/** the visibility state of series label */
	private boolean isVisible;

	/** the series label font */
	protected Font font;

	/** the series label color */
	protected Color color;

	/** the format for series label */
	private String format;

	/** the default label color */
	private static final Color DEFAULT_COLOR = Constants.BLACK;

	/** the default label format */
	private static final String DEFAULT_FORMAT = "############.###########";

	/**
	 * Constructor.
	 */
	public SeriesLabel() {
		font = Constants.TINY_FONT;
		color = DEFAULT_COLOR;
		isVisible = false;
		format = DEFAULT_FORMAT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#getFormat()
	 */
	public String getFormat() {
		return format;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#setFormat(java.lang.String)
	 */
	public void setFormat(String format) {
		if (format == null) {
			this.format = DEFAULT_FORMAT;
		} else {
			this.format = format;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#getForeground()
	 */
	public Color getForeground() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.ISeriesLabel#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (color == null) {
			color = DEFAULT_COLOR;
		}

		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#getFont()
	 */
	public Font getFont() {
		return font;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font) {
		if (font != null && font.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		if (font == null) {
			this.font = Display.getDefault().getSystemFont();
		} else {
			this.font = font;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#isVisible()
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeriesLabel#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	/**
	 * Draws series label.
	 * 
	 * @param gc
	 *            the GC object
	 * @param h
	 *            the horizontal coordinate to draw label
	 * @param v
	 *            the vertical coordinate to draw label
	 * @param ySeriesValue
	 *            the Y series value
	 */
	protected void draw(GC gc, int h, int v, double ySeriesValue) {
		if (!isVisible) {
			return;
		}

		gc.setForeground(color);
		gc.setFont(font);
		String text = new DecimalFormat(format).format(ySeriesValue);
		gc.drawString(text, h, v, true);
	}
}
