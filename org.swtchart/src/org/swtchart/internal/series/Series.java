package org.swtchart.internal.series;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ISeries;
import org.swtchart.ISeriesLabel;
import org.swtchart.Range;
import org.swtchart.internal.axis.Axis;
import org.swtchart.internal.compress.ICompress;

/**
 * Series.
 */
abstract public class Series implements ISeries {

	protected double[] xSeries = null;

	protected double[] ySeries = null;

	protected double minX;

	protected double maxX;

	protected double minY;

	protected double maxY;

	protected String id;

	protected ICompress compressor;

	protected int xAxisId;

	protected int yAxisId;

	protected boolean visible;

	/** the state indicating whether x series are monotone increasing */
	protected boolean isXMonotoneIncreasing;

	/** the series type */
	protected SeriesType type;

	/** the series label */
	protected ISeriesLabel seriesLabel;

	/** the chart */
	protected Chart chart;

	/** the state indicating if the series is a stacked type */
	protected boolean stackEnabled;

	/** the stack series */
	protected double[] stackSeries;

	/** the default series type */
	protected static final SeriesType DEFAULT_SERIES_TYPE = SeriesType.LINE;

	/**
	 * Constructor.
	 */
	protected Series(Chart chart, String id) {
		super();

		this.chart = chart;
		this.id = id;
		xAxisId = 0;
		yAxisId = 0;
		visible = true;
		type = DEFAULT_SERIES_TYPE;
		stackEnabled = false;
		seriesLabel = new SeriesLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getId()
	 */
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (this.visible == visible) {
			return;
		}

		this.visible = visible;

		((SeriesSet) chart.getSeriesSet()).updateStackAndRiserData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#isVisible()
	 */
	public boolean isVisible() {
		return visible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getType()
	 */
	public SeriesType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#isStackEnabled()
	 */
	public boolean isStackEnabled() {
		return stackEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#enableStack(boolean)
	 */
	public void enableStack(boolean enabled) {
		if (enabled && minY < 0) {
			throw new IllegalArgumentException(
					"Stacked series cannot contain minus values.");
		}

		if (enabled && chart.getAxisSet().getXAxis(xAxisId).isLogScaleEnabled()) {
			throw new IllegalArgumentException(
					"Stacked series cannot be set on log scale axis.");
		}

		if (stackEnabled == enabled) {
			return;
		}

		stackEnabled = enabled;

		((SeriesSet) chart.getSeriesSet()).updateStackAndRiserData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#setXSeries(double[])
	 */
	public void setXSeries(double[] series) {

		if (series == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		} else {

			xSeries = new double[series.length];
			System.arraycopy(series, 0, xSeries, 0, series.length);

			// find the min and max value of y series
			minX = xSeries[0];
			maxX = xSeries[0];
			for (int i = 1; i < xSeries.length; i++) {
				if (minX > xSeries[i]) {
					minX = xSeries[i];
				}
				if (maxX < xSeries[i]) {
					maxX = xSeries[i];
				}

				if (xSeries[i - 1] > xSeries[i]) {
					isXMonotoneIncreasing = false;
				}
			}

			setCompressor();

			compressor.setXSeries(xSeries);
			compressor.setYSeries(ySeries);

			if (minX <= 0) {
				IAxis axis = chart.getAxisSet().getXAxis(xAxisId);
				if (axis != null) {
					axis.enableLogScale(false);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getXSeries()
	 */
	public double[] getXSeries() {
		return xSeries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#setYSeries(double[])
	 */
	public void setYSeries(double[] series) {

		if (series == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
			return;// 'return' is not necessary, but just in case.
		}

		ySeries = new double[series.length];
		System.arraycopy(series, 0, ySeries, 0, series.length);

		// find the min and max value of y series
		minY = ySeries[0];
		maxY = ySeries[0];
		for (int i = 1; i < ySeries.length; i++) {
			if (minY > ySeries[i]) {
				minY = ySeries[i];
			}
			if (maxY < ySeries[i]) {
				maxY = ySeries[i];
			}
		}

		if (xSeries == null || xSeries.length != series.length) {
			xSeries = new double[series.length];
			for (int i = 0; i < series.length; i++) {
				xSeries[i] = i;
			}
			minX = xSeries[0];
			maxX = xSeries[xSeries.length - 1];
			isXMonotoneIncreasing = true;
		}

		setCompressor();

		if (minX <= 0) {
			IAxis axis = chart.getAxisSet().getXAxis(xAxisId);
			if (axis != null) {
				axis.enableLogScale(false);
			}
		}
		if (minY <= 0) {
			IAxis axis = chart.getAxisSet().getYAxis(yAxisId);
			if (axis != null) {
				axis.enableLogScale(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getYSeries()
	 */
	public double[] getYSeries() {
		return ySeries;
	}

	/**
	 * Gets the X range of series.
	 * 
	 * @return the X range of series
	 */
	public Range getXRange() {
		return new Range(minX, maxX);
	}

	/**
	 * Gets the X range of series to draw. This range includes the size of plot
	 * like symbol or bar.
	 * 
	 * @param isLogScale
	 *            true if axis is log scale
	 * @return the X range of series to draw.
	 */
	abstract public Range getXRangeToDraw(boolean isLogScale);

	/**
	 * Gets the Y range of series.
	 * 
	 * @return the Y range of series
	 */
	public Range getYRange() {
		double min = minY;
		double max = maxY;
		Axis xAxis = (Axis) chart.getAxisSet().getXAxis(xAxisId);
		Axis yAxis = (Axis) chart.getAxisSet().getYAxis(yAxisId);
		if (stackEnabled && xAxis.isValidCategoryAxis()) {
			for (int i = 0; i < stackSeries.length; i++) {
				if (max < stackSeries[i]) {
					max = stackSeries[i];
				}
			}
		}
		if (type == SeriesType.BAR && min > 0 && !yAxis.isLogScaleEnabled()) {
			min = 0;
		}
		return new Range(min, max);
	}

	/**
	 * Gets the Y range of series to draw. This range includes the size of plot
	 * like symbol or bar.
	 * 
	 * @param isLogScale
	 *            true if axis is log scale
	 * @return the Y range of series to draw.
	 */
	abstract public Range getYRangeToDraw(boolean isLogScale);

	/**
	 * Gets the compressor.
	 * 
	 * @return the compressor
	 */
	protected ICompress getCompressor() {
		return compressor;
	}

	/**
	 * Sets the compressor.
	 */
	abstract protected void setCompressor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getXAxisId()
	 */
	public int getXAxisId() {
		return xAxisId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#setXAxisId(int)
	 */
	public void setXAxisId(int id) {
		if (xAxisId == id) {
			return;
		}

		IAxis axis = chart.getAxisSet().getXAxis(xAxisId);

		if (minX <= 0 && axis != null && axis.isLogScaleEnabled()) {
			chart.getAxisSet().getXAxis(xAxisId).enableLogScale(false);
		}

		xAxisId = id;

		((SeriesSet) chart.getSeriesSet()).updateStackAndRiserData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getYAxisId()
	 */
	public int getYAxisId() {
		return yAxisId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#setYAxisId(int)
	 */
	public void setYAxisId(int id) {
		yAxisId = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.ISeries#getLabel()
	 */
	public ISeriesLabel getLabel() {
		return seriesLabel;
	}

	/**
	 * Sets the stack series
	 * 
	 * @param stackSeries
	 */
	protected void setStackSeries(double[] stackSeries) {
		this.stackSeries = stackSeries;
	}

	/**
	 * Draws series.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 */
	public void draw(GC gc, int width, int height) {

		if (!visible || width < 0 || height < 0 || ySeries == null) {
			return;
		}

		Rectangle oldRect = gc.getClipping();

		Axis xAxis = (Axis) chart.getAxisSet().getXAxis(getXAxisId());
		Axis yAxis = (Axis) chart.getAxisSet().getYAxis(getYAxisId());
		if (xAxis == null || yAxis == null) {
			return;
		}

		draw(gc, width, height, xAxis, yAxis);

		gc.setClipping(oldRect);
	}

	/**
	 * Draws series.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param width
	 *            the width to draw series
	 * @param height
	 *            the height to draw series
	 * @param xAxis
	 *            the x axis
	 * @param yAxis
	 *            the y axis
	 */
	abstract protected void draw(GC gc, int width, int height, Axis xAxis,
			Axis yAxis);
}
