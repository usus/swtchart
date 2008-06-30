package org.swtchart.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IBarSeries;
import org.swtchart.ILegend;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.internal.series.BarSeries;
import org.swtchart.internal.series.LineSeries;
import org.swtchart.internal.series.Series;

/**
 * A legend for chart.
 */
public class Legend extends Canvas implements ILegend, PaintListener {

	/** the plot chart */
	private Chart chart;

	/** the state indicating the legend visibility */
	private boolean visible;

	/** the margin */
	private static final int MARGIN = 5;

	/** the symbol width */
	private static final int SYMBOL_WIDTH = 20;

	/** the line width */
	private static final int LINE_WIDTH = 2;

	/** the default foreground */
	private static final Color DEFAULT_FOREGROUND = Constants.BLACK;

	/** the default background */
	private static final Color DEFAULT_BACKGROUND = Constants.WHITE;

	/** the default font */
	private static final Font DEFAULT_FONT = Constants.TINY_FONT;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style
	 */
	public Legend(Composite parent, int style) {
		super(parent, style);
		this.chart = (Chart) parent;

		visible = true;
		setFont(DEFAULT_FONT);
		setForeground(DEFAULT_FOREGROUND);
		setBackground(DEFAULT_BACKGROUND);
		addPaintListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {

		if (this.visible == visible) {
			return;
		}

		this.visible = visible;
		chart.updateLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return visible;
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
			font = DEFAULT_FONT;
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
			color = DEFAULT_FOREGROUND;
		}
		super.setForeground(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics
	 * .Color)
	 */
	@Override
	public void setBackground(Color color) {
		if (color == null) {
			color = DEFAULT_BACKGROUND;
		}
		super.setBackground(color);
	}

	/**
	 * Update the layout data.
	 */
	public void updateLayoutData() {

		// find max width of plot id text
		ISeries[] seriesArray = chart.getSeriesSet().getSeries();
		int max = 0;
		for (ISeries series : seriesArray) {
			String id = series.getId();
			int width = Util.getExtentInGC(getFont(), id).x;
			if (width > max) {
				max = width;
			}
		}

		// set the width and height of legend area
		int width = 0;
		if (visible && seriesArray.length != 0) {
			width = max + SYMBOL_WIDTH + MARGIN * 3;
		}
		int step = Util.getExtentInGC(getFont(), "dummy").y + MARGIN;
		int height = MARGIN + seriesArray.length * step;

		setLayoutData(new ChartLayoutData(width, height));
	}

	/**
	 * Draws the symbol of series.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param r
	 *            the rectangle to draw the symbol of series
	 */
	protected void drawSymbol(GC gc, Series series, Rectangle r) {

		if (!visible) {
			return;
		}
		
		if (series instanceof ILineSeries) {
			// draw plot line
			gc.setForeground(((ILineSeries) series).getLineColor());
			gc.setLineWidth(LINE_WIDTH);
			int lineStyle = Util.getIndexDefinedInSWT(((ILineSeries) series)
					.getLineStyle());
			int x = r.x;
			int y = (int) (r.y + r.height / 2d);
			if (lineStyle != SWT.NONE) {
				gc.setLineStyle(lineStyle);
				gc.drawLine(x, y, x + SYMBOL_WIDTH, y);
			}

			// draw series symbol
			((LineSeries) series).drawSeriesSymbol(gc, x + SYMBOL_WIDTH/2, y);
		} else if (series instanceof IBarSeries) {
			// draw riser
			gc.setBackground(((IBarSeries) series).getBarColor());
			int size = BarSeries.INITIAL_WIDTH_IN_PIXELS;
			int x = (int) (r.x + size / 2d);
			int y = (int) (r.y - size / 2d + r.height / 2d);
			gc.fillRectangle(x, y, size, size);
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

		if (!visible) {
			return;
		}

		GC gc = e.gc;
		gc.setFont(getFont());
		int width = getSize().x;
		int height = getSize().y;
		int step = gc.textExtent("dummy").y + MARGIN;
		ISeries[] seriesArray = chart.getSeriesSet().getSeries();
		if (seriesArray.length == 0) {
			return;
		}

		// draw frame
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setLineWidth(1);
		gc.setForeground(Constants.GRAY);
		gc.drawRectangle(0, 0, width - 1, height - 1);

		// draw content
		for (int i = 0; i < seriesArray.length; i++) {

			// draw plot line, symbol etc
			int xPosition = MARGIN;
			int yPosition = MARGIN + i * step;
			drawSymbol(gc, (Series) seriesArray[i], new Rectangle(xPosition,
					yPosition, SYMBOL_WIDTH, gc.textExtent("dummy").y));

			// draw plot id
			String id = seriesArray[i].getId();
			gc.setBackground(getBackground());
			gc.setForeground(getForeground());
			xPosition = SYMBOL_WIDTH + 2 * MARGIN;
			yPosition = MARGIN + i * step;
			gc.drawText(id, xPosition, yPosition);
		}
	}
}
