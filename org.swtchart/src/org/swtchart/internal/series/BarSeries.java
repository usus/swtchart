package org.swtchart.internal.series;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IBarSeries;
import org.swtchart.Range;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.CompressBarSeries;
import org.swtchart.internal.compress.CompressScatterSeries;

/**
 * Bar series.
 */
public class BarSeries extends Series implements IBarSeries {

	/** the riser index in a category */
	private int riserIndex;

	/** the riser color */
	private Color barColor;

	/** the padding */
	private int padding;

	/** the initial bar padding in percentage */
	public static final int INITIAL_PADDING = 20;

	/** the alpha value */
	private static final int ALPHA = 0xD0;

	/** the initial bar width in pixels */
	public static final int INITIAL_WIDTH_IN_PIXELS = 10;

	/** the margin in pixels attached at the minimum/maximum plot */
	private static final int MARGIN_AT_MIN_MAX_PLOT = 6;

	/** the default bar color */
	private static final RGB DEFAULT_BAR_COLOR = Constants.LIGHT_BLUE;

	/**
	 * Constructor.
	 */
	protected BarSeries(Chart chart, String id) {
		super(chart, id);

		barColor = new Color(Display.getDefault(), DEFAULT_BAR_COLOR);
		padding = INITIAL_PADDING;
		type = SeriesType.BAR;

		compressor = new CompressBarSeries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IBarSeries#getBarPadding()
	 */
	public int getBarPadding() {
		return padding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IBarSeries#setBarPadding(int)
	 */
	public void setBarPadding(int padding) {
		if (padding < 0 || padding > 100) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.padding = padding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IBarSeries#getBarColor()
	 */
	public Color getBarColor() {
		return barColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.IBarSeries#setBarColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBarColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}

		if (color == null) {
			this.barColor = new Color(Display.getDefault(), DEFAULT_BAR_COLOR);
		} else {
			this.barColor = color;
		}
	}

	/**
	 * Sets the index of riser in a category.
	 * 
	 * @param riserIndex
	 *            the index of riser in a category
	 */
	protected void setRiserIndex(int riserIndex) {
		this.riserIndex = riserIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.series.Series#setCompressor()
	 */
	@Override
	protected void setCompressor() {
		if (isXMonotoneIncreasing) {
			compressor = new CompressBarSeries();
		} else {
			compressor = new CompressScatterSeries();
		}
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

		double lowerRiserWidth;
		double upperRiserWidth;
		if (isLogScale) {
			lowerRiserWidth = getRaiserWidth(xSeries, 0, range, width, true);
			upperRiserWidth = getRaiserWidth(xSeries, xSeries.length - 1,
					range, width, true);
		} else {
			lowerRiserWidth = getRaiserWidth(xSeries, 0, range, width, false);
			upperRiserWidth = getRaiserWidth(xSeries, xSeries.length - 1,
					range, width, false);
		}

		// calculate a range which has margin
		double lowerPlotMargin = lowerRiserWidth / 2d + MARGIN_AT_MIN_MAX_PLOT;
		double upperPlotMargin = upperRiserWidth / 2d + MARGIN_AT_MIN_MAX_PLOT;
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

		// get axis width in pixel
		int height = chart.getPlotArea().getSize().y;
		if (height <= 0) {
			return range;
		}

		// calculate a range which has margin
		double lowerPlotMargin = (range.lower == 0) ? 0
				: MARGIN_AT_MIN_MAX_PLOT;
		double upperPlotMargin = (range.upper == 0) ? 0
				: MARGIN_AT_MIN_MAX_PLOT;
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
			drawBarSeriesOnCagetoryAxis(gc, width, height, xAxis, yAxis);
		} else {
			drawBarSeries(gc, width, height, xAxis, yAxis);
		}
	}

	/**
	 * Draws bar series on category axis.
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
	private void drawBarSeriesOnCagetoryAxis(GC gc, int width, int height,
			Axis xAxis, Axis yAxis) {
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
		Range xRange = xAxis.getRange();
		Range yRange = yAxis.getRange();
		double[] series;
		if (stackEnabled) {
			series = stackSeries;
		} else {
			series = getYSeries();
		}

		for (int i = (int) xRange.lower; i < xRange.upper + 1; i++) {
			if (i >= series.length) {
				break;
			}
			double x = (i - xRange.lower + 0.5)
					/ (xRange.upper - xRange.lower + 1) * xWidth;
			double riserwidth = xWidth / (xRange.upper - xRange.lower + 1);

			double riserHeight;
			int y;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(yRange.upper);
				double digitMin = Math.log10(yRange.lower);
				y = (int) ((Math.log10(series[i]) - digitMin)
						/ (digitMax - digitMin) * yWidth);
				riserHeight = getRiserHeight(i, yRange, yWidth, true);
			} else {
				y = (int) ((series[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
				riserHeight = getRiserHeight(i, yRange, yWidth, false);
			}
			if (y > yWidth) {
				riserHeight -= y - yWidth;
				y = yWidth;
			}

			riserwidth *= (100 - padding) / 100d;

			int riserCnt = xAxis.getNumRisers();
			if (riserCnt > 1) {
				x = x - riserwidth / 2d + riserwidth / riserCnt
						* (riserIndex + 0.5);
				riserwidth /= riserCnt;
			}

			if (isHorizontal) {
				drawRiser(gc, x, height - y, riserwidth, riserHeight);

				((SeriesLabel) seriesLabel).draw(gc, (int) x, height - y,
						series[i]);
			} else {
				drawRiser(gc, y, height - x, riserwidth, riserHeight);

				((SeriesLabel) seriesLabel).draw(gc, y, (int) (height - x),
						series[i]);
			}
		}
	}

	/**
	 * Gets the riser height.
	 * 
	 * @param index
	 *            the series index
	 * @param yRange
	 *            the y axis range
	 * @param yAxisWidth
	 *            the y axis width
	 * @param isLogScale
	 *            true if the axis is log scale
	 * @return the symbol size in pixels
	 */
	private double getRiserHeight(int index, Range yRange, int yAxisWidth,
			boolean isLogScale) {
		double height;

		if (isLogScale) {
			double digitMax = Math.log10(yRange.upper);
			double digitMin = Math.log10(yRange.lower);
			height = (Math.log10(ySeries[index]) - digitMin)
					/ (digitMax - digitMin) * yAxisWidth;
		} else {
			height = ySeries[index] / (yRange.upper - yRange.lower)
					* yAxisWidth;
		}
		return height;
	}

	/**
	 * Draws bar series.
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
	private void drawBarSeries(GC gc, int width, int height, Axis xAxis,
			Axis yAxis) {

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
		Range xRange = xAxis.getRange();
		Range yRange = yAxis.getRange();
		double[] xCompressedSeries = compressor.getCompressedXSeries();
		double[] yCompressedSeries = compressor.getCompressedYSeries();

		for (int i = 0; i < xCompressedSeries.length; i++) {
			int x;
			double riserwidth;
			if (xAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(xRange.upper);
				double digitMin = Math.log10(xRange.lower);
				x = (int) ((Math.log10(xCompressedSeries[i]) - digitMin)
						/ (digitMax - digitMin) * xWidth);
			} else {
				x = (int) ((xCompressedSeries[i] - xRange.lower)
						/ (xRange.upper - xRange.lower) * xWidth);
			}
			riserwidth = getRaiserWidth(xCompressedSeries, i, xRange, xWidth,
					false);

			int y;
			double riserHeight;
			if (yAxis.isLogScaleEnabled()) {
				double digitMax = Math.log10(yRange.upper);
				double digitMin = Math.log10(yRange.lower);
				y = (int) ((Math.log10(yCompressedSeries[i]) - digitMin)
						/ (digitMax - digitMin) * yWidth);
				riserHeight = y;
			} else {
				y = (int) ((yCompressedSeries[i] - yRange.lower)
						/ (yRange.upper - yRange.lower) * yWidth);
				riserHeight = y + yRange.lower / (yRange.upper - yRange.lower)
						* yWidth;
			}
			if (y > yWidth) {
				riserHeight -= y - yWidth;
				y = yWidth;
			}
			riserwidth *= (100 - padding) / 100d;

			int riserCnt = xAxis.getNumRisers();
			if (riserCnt > 1) {
				x = (int) (x - riserwidth / 2d + riserwidth / riserCnt
						* (riserIndex + 0.5));
				riserwidth /= riserCnt;
			}

			if (isHorizontal) {
				drawRiser(gc, x, height - y, riserwidth, riserHeight);
				((SeriesLabel) seriesLabel).draw(gc, x, height - y,
						yCompressedSeries[i]);
			} else {
				drawRiser(gc, y, height - x, riserwidth, riserHeight);
				((SeriesLabel) seriesLabel).draw(gc, y, height - x,
						yCompressedSeries[i]);
			}
		}
	}

	/**
	 * Gets the riser width.
	 * 
	 * @param series
	 *            the X series
	 * @param index
	 *            the series index
	 * @param xRange
	 *            the x axis range
	 * @param xAxisWidth
	 *            the x axis width
	 * @param isLogScale
	 *            true if the axis is log scale
	 * @return the symbol size in pixels
	 */
	private double getRaiserWidth(double[] series, int index, Range xRange,
			int xAxisWidth, boolean isLogScale) {
		double width;
		if (series.length == 1) {
			width = INITIAL_WIDTH_IN_PIXELS;
		} else if (index == 0) {
			if (isLogScale) {
				width = Math.log10(series[1]) - Math.log10(series[0])
						/ (Math.log10(xRange.upper) - Math.log10(xRange.lower))
						* xAxisWidth;
			} else {
				width = (series[1] - series[0]) / (xRange.upper - xRange.lower)
						* xAxisWidth;
			}
		} else if (index == series.length - 1) {
			if (isLogScale) {
				width = Math.log10(series[series.length - 1]
						- Math.log10(series[series.length - 2]))
						/ (Math.log10(xRange.upper) - Math.log10(xRange.lower))
						* xAxisWidth;
			} else {
				width = (series[series.length - 1] - series[series.length - 2])
						/ (xRange.upper - xRange.lower) * xAxisWidth;
			}
		} else {
			if (isLogScale) {
				double plotStep = Math.min(Math.log10(series[index + 1])
						- Math.log10(series[index]), Math.log10(series[index])
						- Math.log10(series[index - 1]));
				width = plotStep
						/ (Math.log10(xRange.upper) - Math.log10(xRange.lower))
						* xAxisWidth;
			} else {
				double plotStep = Math.min(series[index + 1] - series[index],
						series[index] - series[index - 1]);
				width = plotStep / (xRange.upper - xRange.lower) * xAxisWidth;

			}
		}

		// symbol size should be at least more than 1
		if (width == 0) {
			width = 1;
		}

		return width;
	}

	/**
	 * Draws symbol.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param h
	 *            the horizontal coordinate
	 * @param v
	 *            the vertical coordinate
	 * @param riserWidth
	 *            the riser width
	 * @param riserHeight
	 *            the riser height
	 */
	private void drawRiser(GC gc, double h, double v, double riserWidth,
			double riserHeight) {
		gc.setBackground(barColor);

		int alpha = gc.getAlpha();
		gc.setAlpha(ALPHA);

		int x, y, width, height;
		if (chart.getOrientation() == SWT.HORIZONTAL) {
			x = (int) Math.floor(h - riserWidth / 2d);
			y = (int) v;
			width = (int) Math.ceil(riserWidth);
			width = (width == 0) ? 1 : width;
			height = (int) riserHeight;
		} else {
			x = (int) (h - riserHeight);
			y = (int) Math.floor(v - riserWidth / 2d);
			width = (int) riserHeight;
			height = (int) Math.ceil(riserWidth);
			height = (height == 0) ? 1 : height;
		}
		gc.fillRectangle(x, y, width, height);

		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(getFrameColor(barColor));
		gc.drawRectangle(x, y, width, height);

		gc.setAlpha(alpha);
	}

	/**
	 * Gets the color for riser frame. The color will be darker or lighter than
	 * the given color.
	 * 
	 * @param color
	 *            the riser color
	 * @return the riser frame color
	 */
	private Color getFrameColor(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		red *= (red > 128) ? 0.8 : 1.2;
		green *= (green > 128) ? 0.8 : 1.2;
		blue *= (blue > 128) ? 0.8 : 1.2;

		return new Color(color.getDevice(), red, green, blue);
	}
}
