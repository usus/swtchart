package org.swtchart.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ITitle;

/**
 * A base class for title.
 */
public class Title extends Canvas implements ITitle, PaintListener {

	/** the chart */
	protected Chart chart;

	/** the title text */
	protected String text;

	/** the visibility state of axis */
	protected boolean isVisible;

	/** the default font */
	private static final int DEFAULT_FONT_SIZE = Constants.LARGE_FONT_SIZE;

	/** the default color */
	private static final RGB DEFAULT_FOREGROUND = Constants.BLUE;

	/** the default text */
	private static final String DEFAULT_TEXT = "";

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style
	 * @param chart
	 *            the chart
	 */
	public Title(Chart parent, int style) {
		super(parent, style);

		this.chart = parent;
		text = getDefaultText();
		isVisible = true;

		setFont(new Font(Display.getDefault(), "Tahoma", DEFAULT_FONT_SIZE,
				SWT.BOLD));
		setForeground(new Color(Display.getDefault(), DEFAULT_FOREGROUND));

		addPaintListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ITitle#setText(java.lang.String)
	 */
	public void setText(String text) {
		if (text == null) {
			this.text = getDefaultText();
		} else {
			this.text = text;
		}
		chart.updateLayout(); // text could be changed to blank
	}

	/**
	 * Gets the default title text.
	 * 
	 * @return the default title text
	 */
	protected String getDefaultText() {
		return DEFAULT_TEXT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ITitle#getText()
	 */
	public String getText() {
		return text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Canvas#setFont(org.eclipse.swt.graphics.Font)
	 */
	@Override
	public void setFont(Font font) {
		if (font == null) {
			font = new Font(Display.getDefault(), "Tahoma", DEFAULT_FONT_SIZE,
					SWT.BOLD);
		}
		super.setFont(font);
		chart.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics
	 * .Color)
	 */
	@Override
	public void setForeground(Color color) {
		if (color == null) {
			color = new Color(Display.getDefault(), DEFAULT_FOREGROUND);
		}
		super.setForeground(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean isVisible) {
		if (this.isVisible == isVisible) {
			return;
		}

		this.isVisible = isVisible;
		chart.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Gets the state indicating if showing title horizontally.
	 * 
	 * @return the state indicating if showing title horizontally
	 */
	protected boolean isHorizontal() {
		return true;
	}

	/**
	 * Updates the title layout data.
	 */
	public void updateLayoutData() {
		int height;
		int width;
		if (isVisible() && !text.trim().equals("")) {
			Point p = Util.getExtentInGC(getFont(), text);
			width = p.x;
			height = p.y;
		} else {
			width = 0;
			height = 0;
		}
		if (isHorizontal()) {
			setLayoutData(new ChartLayoutData(width, height));
		} else {
			setLayoutData(new ChartLayoutData(height, width));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events
	 * .PaintEvent)
	 */
	public void paintControl(PaintEvent e) {

		if (text == null || text.equals("") || !isVisible) {
			return;
		}

		int width = getSize().x;
		int height = getSize().y;
		GC gc = e.gc;
		gc.setForeground(getForeground());
		gc.setFont(getFont());

		if (isHorizontal()) {
			int textWidth = gc.textExtent(text).x;
			int x = (int) (width / 2.0 - textWidth / 2.0);
			if (x < 0) {
				// this happens when window size is too small
				x = 0;
			}

			gc.drawString(text, x, 0, true);
		} else {
			int textWidth = 0;
			int textHeight = 0;

			gc.setFont(getFont());
			Color background = gc.getBackground();
			textWidth = gc.textExtent(text).x;
			textHeight = gc.textExtent(text).y;
			Image image = new Image(Display.getCurrent(), textWidth, textHeight);
			GC tmpGc = new GC(image);
			tmpGc.setForeground(getForeground());
			tmpGc.setBackground(background);
			tmpGc.setFont(getFont());
			tmpGc.drawText(text, 0, 0);
			ImageData imageData = Util.rotate(image.getImageData());
			image.dispose();
			tmpGc.dispose();

			if (imageData == null) {
				return;
			}

			int y = (int) (height / 2.0 - textWidth / 2.0);
			if (y < 0) {
				y = 0;
			}
			image = new Image(Display.getCurrent(), imageData);
			e.gc.drawImage(image, 0, y);
			image.dispose();
		}
	}
}