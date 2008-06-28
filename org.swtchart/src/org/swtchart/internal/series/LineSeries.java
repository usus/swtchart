package org.swtchart.internal.series;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.Range;
import org.swtchart.internal.Util;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressLineSeries;
import org.swtchart.internal.compress.CompressScatterSeries;

/**
 * Line series.
 */
public class LineSeries extends Series implements ILineSeries {

	/** the symbol size in pixel */
	private int symbolSize;

	/** the symbol color */
	private Color symbolColor;

	/** the symbol type */
	private PlotSymbolType symbolType;

	/** the line style */
	private LineStyle lineStyle;

	/** the line color */
	private Color lineColor;

	/** the default line style */
	private static final LineStyle DEFAULT_LINE_STYLE = LineStyle.SOLID;

	/** the default line color */
	private static final Color DEFAULT_LINE_COLOR = Constants.GRAY;

	/** the default symbol color */
	private static final Color DEFAULT_COLOR = Constants.BLUE;

	/** the default symbol size */
	private static final int DEFAULT_SIZE = 4;

	/** the default symbol type */
	private static final PlotSymbolType DEFAULT_SYMBOL_TYPE = PlotSymbolType.CIRCLE;

	/** the margin in pixels attached at the minimum/maximum plot */
	private static final int MARGIN_AT_MIN_MAX_PLOT = 6;

	/**
	 * Constructor.
	 */
	protected LineSeries(Chart chart, String id) {
		super(chart, id);

		symbolSize = 4;
		symbolColor = DEFAULT_COLOR;
		symbolType = DEFAULT_SYMBOL_TYPE;

		lineStyle = DEFAULT_LINE_STYLE;
		lineColor = DEFAULT_LINE_COLOR;

		compressor = new CompressLineSeries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#getLineStyle()
	 */
	public LineStyle getLineStyle() {
		return lineStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#setLineStyle(org.swtchart.LineStyle)
	 */
	public void setLineStyle(LineStyle style) {
		if (style == null) {
			this.lineStyle = DEFAULT_LINE_STYLE;
		} else {
			this.lineStyle = style;
			if (compressor instanceof CompressScatterSeries) {
				((CompressScatterSeries) compressor)
						.setLineVisible(style != LineStyle.NONE);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#getLineColor()
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.ILineSeries#setLineColor(org.eclipse.swt.graphics.Color)
	 */
	public void setLineColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (color == null) {
			this.lineColor = DEFAULT_LINE_COLOR;
		} else {
			this.lineColor = color;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#getSymbolType()
	 */
	public PlotSymbolType getSymbolType() {
		return symbolType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.swtchart.ILineSeries#setSymbolType(org.swtchart.ILineSeries.
	 * PlotSymbolType)
	 */
	public void setSymbolType(PlotSymbolType type) {
		if (type == null) {
			this.symbolType = DEFAULT_SYMBOL_TYPE;
		} else {
			this.symbolType = type;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#getSymbolSize()
	 */
	public int getSymbolSize() {
		return symbolSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#setSymbolSize(int)
	 */
	public void setSymbolSize(int size) {
		if (size <= 0) {
			size = DEFAULT_SIZE;
		}
		this.symbolSize = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ILineSeries#getSymbolColor()
	 */
	public Color getSymbolColor() {
		return symbolColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.ILineSeries#setSymbolColor(org.eclipse.swt.graphics.Color)
	 */
	public void setSymbolColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (color == null) {
			this.symbolColor = DEFAULT_COLOR;
		} else {
			this.symbolColor = color;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.series.Series#setCompressor()
	 */
	@Override
	protected void setCompressor() {
		if (isXMonotoneIncreasing) {
			compressor = new CompressLineSeries();
		} else {
			compressor = new CompressScatterSeries();
			((CompressScatterSeries) compressor)
					.setLineVisible(getLineStyle() != LineStyle.NONE);
		}

		compressor.setXSeries(xSeries);
		compressor.setYSeries(ySeries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.series.Series#getXRangeToDraw(boolean)
	 */
	@Override
	public Range getXRangeToDraw(boolean isLogScale) {

		Range range = getXRange();

		// get axis width in pixel
		int width = chart.getPlotArea().getSize().x;
		if (width <= 0) {
			return range;
		}

		// calculate a range which has margin
		double lowerPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;
		double upperPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;
		if (isLogScale) {
			double digitMax = Math.log10(range.upper);
			double digitMin = Math.log10(range.lower);

			// log(upper) - log(max) = 10 * (log(max) - log(min)) / width
			double upper = Math.pow(10, digitMax + upperPlotMargin
					* ((digitMax - digitMin) / width));

			// log(min) - log(lower) = 10 * (log(max) - log(min)) / width
			double lower = Math.pow(10, digitMin - lowerPlotMargin
					* ((digitMax - digitMin) / width));

			range = new Range(lower, upper);
		} else {
			double lower = range.lower - (range.upper - range.lower)
					* (lowerPlotMargin / width);
			double upper = range.upper + (range.upper - range.lower)
					* (upperPlotMargin / width);

			range = new Range(lower, upper);
		}
		return range;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.series.Series#getYRangeToDraw(boolean)
	 */
	@Override
	public Range getYRangeToDraw(boolean isLogScale) {

		Range range = getYRange();

		// get axis height in pixel
		int height = chart.getPlotArea().getSize().y;
		if (height <= 0) {
			return range;
		}

		// calculate a range which has margin
		double lowerPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;
		double upperPlotMargin = getSymbolSize() + MARGIN_AT_MIN_MAX_PLOT;
		if (isLogScale) {
			double digitMax = Math.log10(range.upper);
			double digitMin = Math.log10(range.lower);

			// log(upper) - log(max) = 10 * (log(max) - log(min)) / width
			double upper = Math.pow(10, digitMax + upperPlotMargin
					* ((digitMax - digitMin) / height));

			// log(min) - log(lower) = 10 * (log(max) - log(min)) / width
			double lower = Math.pow(10, digitMin - lowerPlotMargin
					* ((digitMax - digitMin) / height));

			range = new Range(lower, upper);
		} else {
			double lower = range.lower - (range.upper - range.lower)
					* (lowerPlotMargin / height);
			double upper = range.upper + (range.upper - range.lower)
					* (upperPlotMargin / height);

			range = new Range(lower, upper);
		}
		return range;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.internal.series.Series#draw(org.eclipse.swt.graphics.GC,
	 * int, int, org.swtchart.internal.axis.Axis,
	 * org.swtchart.internal.axis.Axis)
	 */
	@Override
	protected void draw(GC gc, int width, int height, Axis xAxis, Axis yAxis) {
		if (xAxis.isValidCategoryAxis()) {
			if (lineStyle != LineStyle.NONE) {
				drawSeriesLinesOnCategoryAxis(gc, width, height, xAxis, yAxis);
			}
			if (getSymbolType() != PlotSymbolType.NONE) {
				drawSeriesSymbolsOnCategoryAxis(gc, width, height, xAxis, yAxis);
			}
		} else {
			if (lineStyle != LineStyle.NONE) {
				drawSeriesLines(gc, width, height, xAxis, yAxis);
			}
			if (getSymbolType() != PlotSymbolType.NONE) {
				drawSeriesSymbols(gc, width, height, xAxis, yAxis);
			}
		}
	}

	/**
	 * Draws series symbol.
	 * 
	 * @param gc
	 *            the GC object
	 * @param h
	 *            the horizontal coordinate to draw symbol
	 * @param v
	 *            the vertical coordinate to draw symbol
	 */
	public void drawSeriesSymbol(GC gc, int h, int v) {
		gc.setAntialias(SWT.ON);
		gc.setForeground(symbolColor);
		gc.setBackground(symbolColor);

		switch (symbolType) {
		case CIRCLE:
			gc.fillOval(h - symbolSize, v - symbolSize, symbolSize * 2,
					symbolSize * 2);
			break;
		case SQUARE:
			gc.fillRectangle(h - symbolSize, v - symbolSize, symbolSize * 2,
					symbolSize * 2);
			break;
		case DIAMOND:
			int[] diamondArray = { h, v - symbolSize, h + symbolSize, v, h,
					v + symbolSize, h - symbolSize, v };
			gc.fillPolygon(diamondArray);
			break;
		case TRIANGLE:
			int[] triangleArray = { h, v - symbolSize, h + symbolSize,
					v + symbolSize, h - symbolSize, v + symbolSize };
			gc.fillPolygon(triangleArray);
			break;
		case INVERTED_TRIANGLE:
			int[] invertedTriangleArray = { h, v + symbolSize, h + symbolSize,
					v - symbolSize, h - symbolSize, v - symbolSize };
			gc.fillPolygon(invertedTriangleArray);
			break;
		case CROSS:
			gc.drawLine(h - symbolSize, v - symbolSize, h + symbolSize, v
					+ symbolSize);
			gc.drawLine(h - symbolSize, v + symbolSize, h + symbolSize, v
					- symbolSize);
			break;
		case PLUS:
			gc.drawLine(h, v - symbolSize, h, v + symbolSize);
			gc.drawLine(h - symbolSize, v, h + symbolSize, v);
			break;
		case NONE:
		default:
			break;
		}
		gc.setAntialias(SWT.OFF);
	}

	/**
	 * Draws line series on category axis.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 * @param xAxis
	 *            the xAxis
	 * @param yAxis
	 *            the xAxis
	 */
	private void drawSeriesLinesOnCategoryAxis(GC gc, int width, int height,
			Axis xAxis, Axis yAxis) {
		Range xRange = xAxis.getRange();
		Range yRange = yAxis.getRange();
		int xWidth;
		int yWidth;
		boolean isHorizontal = xAxis.isHorizontalAxis();
		if (isHorizontal) {
			xWidth = width;
			yWidth = height;
		} else {
			xWidth = height;
			yWidth = width;
		}

		gc.setLineStyle(Util.getIndexDefinedInSWT(lineStyle));
		gc.setForeground(lineColor);
		for (int i = (int) xRange.lower - 1; i < (int) xRange.upper + 1; i++) {
			if (i < 0) {
				continue;
			}
			if (i >= ySeries.length - 1) {
				break;
			}
			int x1 = (int) ((i + 0.5 - (int) xRange.lower)
					/ ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);
			int x2 = (int) ((i + 1.5 - (int) xRange.lower)
					/ ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);

			int y1;
			int y2;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(yRange.upper);
				double digitMin = Math.log10(yRange.lower);
				if (i + (int) xRange.lower + 1 > ySeries.length) {
					break;
				}
				y1 = (int) ((Math.log10(ySeries[i]) - digitMin)
						/ (digitMax - digitMin) * yWidth);

				y2 = (int) ((Math.log10(ySeries[i + 1]) - digitMin)
						/ (digitMax - digitMin) * yWidth);
			} else if (stackEnabled) {
				y1 = (int) ((stackSeries[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);

				y2 = (int) ((stackSeries[i + 1] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
			} else {
				y1 = (int) ((ySeries[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);

				y2 = (int) ((ySeries[i + 1] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
			}

			if (isHorizontal) {
				gc.drawLine(x1, height - y1, x2, height - y2);
			} else {
				gc.drawLine(y1, height - x1, y2, height - x2);
			}
		}
	}

	/**
	 * Draws series symbol on category axis.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 * @param xAxis
	 *            the xAxis
	 * @param yAxis
	 *            the xAxis
	 */
	private void drawSeriesSymbolsOnCategoryAxis(GC gc, int width, int height,
			Axis xAxis, Axis yAxis) {
		Range xRange = xAxis.getRange();
		Range yRange = yAxis.getRange();
		int xWidth;
		int yWidth;
		boolean isHorizontal = xAxis.isHorizontalAxis();
		if (isHorizontal) {
			xWidth = width;
			yWidth = height;
		} else {
			xWidth = height;
			yWidth = width;
		}

		for (int i = (int) xRange.lower; i < (int) xRange.upper + 1; i++) {
			if (i >= ySeries.length) {
				break;
			}
			int x = (int) ((i - (int) xRange.lower + 0.5)
					/ ((int) xRange.upper - (int) xRange.lower + 1) * xWidth);

			int y;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(yRange.upper);
				double digitMin = Math.log10(yRange.lower);
				y = (int) ((Math.log10(ySeries[i]) - digitMin)
						/ (digitMax - digitMin) * yWidth);
			} else if (stackEnabled) {
				y = (int) ((stackSeries[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
			} else {
				y = (int) ((ySeries[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
			}

			if (isHorizontal) {
				drawSeriesSymbol(gc, x, height - y);
				((SeriesLabel) seriesLabel).draw(gc, x, height - y, ySeries[i]);
			} else {
				drawSeriesSymbol(gc, y, height - x);
				((SeriesLabel) seriesLabel).draw(gc, y, height - x, ySeries[i]);
			}
		}

	}

	/**
	 * Draws line series.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 * @param xAxis
	 *            the xAxis
	 * @param yAxis
	 *            the xAxis
	 */
	private void drawSeriesLines(GC gc, int width, int height, Axis xAxis,
			Axis yAxis) {
		Range hRange;
		Range vRange;
		double[] hSeries;
		double[] vSeries;
		if (xAxis.isHorizontalAxis()) {
			hRange = xAxis.getRange();
			vRange = yAxis.getRange();
			hSeries = compressor.getCompressedXSeries();
			vSeries = compressor.getCompressedYSeries();
		} else {
			hRange = yAxis.getRange();
			vRange = xAxis.getRange();
			hSeries = compressor.getCompressedYSeries();
			vSeries = compressor.getCompressedXSeries();
		}

		gc.setLineStyle(Util.getIndexDefinedInSWT(lineStyle));
		gc.setForeground(lineColor);
		for (int i = 0; i < hSeries.length - 1; i++) {
			int h1;
			int h2;
			if (xAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(hRange.upper);
				double digitMin = Math.log10(hRange.lower);
				h1 = (int) ((Math.log10(hSeries[i]) - digitMin)
						/ (digitMax - digitMin) * width);
				h2 = (int) ((Math.log10(hSeries[i + 1]) - digitMin)
						/ (digitMax - digitMin) * width);
			} else {
				h1 = (int) ((hSeries[i] - hRange.lower)
						/ (hRange.upper - hRange.lower) * width);
				h2 = (int) ((hSeries[i + 1] - hRange.lower)
						/ (hRange.upper - hRange.lower) * width);
			}
			int v1;
			int v2;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(vRange.upper);
				double digitMin = Math.log10(vRange.lower);
				if (i + (int) hRange.lower + 1 > ySeries.length) {
					break;
				}
				v1 = (int) ((Math.log10(vSeries[i]) - digitMin)
						/ (digitMax - digitMin) * height);

				v2 = (int) ((Math.log10(vSeries[i + 1]) - digitMin)
						/ (digitMax - digitMin) * height);
			} else {
				v1 = (int) ((vSeries[i] - vRange.lower)
						/ (vRange.upper - vRange.lower) * height);

				v2 = (int) ((vSeries[i + 1] - vRange.lower)
						/ (vRange.upper - vRange.lower) * height);
			}

			gc.drawLine(h1, height - v1, h2, height - v2);
		}
	}

	/**
	 * Draws series symbol.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 * @param xAxis
	 *            the xAxis
	 * @param yAxis
	 *            the xAxis
	 */
	private void drawSeriesSymbols(GC gc, int width, int height, Axis xAxis,
			Axis yAxis) {
		Range hRange;
		Range vRange;
		double[] hSeries;
		double[] vSeries;
		if (xAxis.isHorizontalAxis()) {
			hRange = xAxis.getRange();
			vRange = yAxis.getRange();
			hSeries = compressor.getCompressedXSeries();
			vSeries = compressor.getCompressedYSeries();
		} else {
			hRange = yAxis.getRange();
			vRange = xAxis.getRange();
			hSeries = compressor.getCompressedYSeries();
			vSeries = compressor.getCompressedXSeries();
		}

		for (int i = 0; i < hSeries.length; i++) {
			int hCoordinate;
			if (xAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(hRange.upper);
				double digitMin = Math.log10(hRange.lower);
				hCoordinate = (int) ((Math.log10(hSeries[i]) - digitMin)
						/ (digitMax - digitMin) * width);
			} else {
				hCoordinate = (int) ((hSeries[i] - hRange.lower)
						/ (hRange.upper - hRange.lower) * width);
			}

			int vCoordinate;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(vRange.upper);
				double digitMin = Math.log10(vRange.lower);
				vCoordinate = (int) ((Math.log10(vSeries[i]) - digitMin)
						/ (digitMax - digitMin) * height);
			} else {
				vCoordinate = (int) ((vSeries[i] - vRange.lower)
						/ (vRange.upper - vRange.lower) * height);
			}

			drawSeriesSymbol(gc, hCoordinate, height - vCoordinate);
			((SeriesLabel) seriesLabel).draw(gc, hCoordinate, height
					- vCoordinate, vSeries[i]);
		}
	}
}
