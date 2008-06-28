package org.swtchart.internal.compress;

import java.util.ArrayList;

/**
 * A base class for compressor providing default implementations.
 */
public abstract class Compress implements ICompress {

	/** the previous X grid index */
	protected int previousXGridIndex;

	/** the previous Y grid index */
	protected int previousYGridIndex;

	/** the configuration for compressor */
	protected CompressConfig config;

	/** the previous configuration for compressor */
	protected CompressConfig prevConfig;

	/** the flag indicating whether the data is compressed */
	protected boolean compressed;

	/** the source X series to be compressed */
	protected double[] xSeries = null;

	/** the source Y series to be compressed */
	protected double[] ySeries = null;

	/** the compressed X series */
	protected transient double[] compressedXSeries = null;

	/** the compressed Y series */
	protected transient double[] compressedYSeries = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.compress.ICompress#setXSeries(double[])
	 */
	public void setXSeries(double[] xSeries) {
		this.xSeries = xSeries;
		compressedXSeries = xSeries;
		compressed = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.compress.ICompress#setYSeries(double[])
	 */
	public void setYSeries(double[] ySeries) {
		this.ySeries = ySeries;
		compressedYSeries = ySeries;
		compressed = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.compress.ICompress#getCompressedXSeries()
	 */
	public double[] getCompressedXSeries() {
		return compressedXSeries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.compress.ICompress#getCompressedYSeries()
	 */
	public double[] getCompressedYSeries() {
		return compressedYSeries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.internal.compress.ICompress#compress(org.swtchart.internal
	 * .compress.CompressConfig)
	 */
	final public boolean compress(CompressConfig compressConfig) {

		if ((compressConfig.equals(prevConfig) && compressed) || xSeries == null
				|| ySeries == null) {
			return false;
		}

		// store the previous configuration
		prevConfig = new CompressConfig(compressConfig);

		this.config = compressConfig;
		previousXGridIndex = -1;
		previousYGridIndex = -1;

		ArrayList<Double> xList = new ArrayList<Double>();
		ArrayList<Double> yList = new ArrayList<Double>();

		// add necessary plots to the array
		addNecessaryPlots(xList, yList);

		compressedXSeries = new double[xList.size()];
		compressedYSeries = new double[yList.size()];
		for (int i = 0; i < xList.size(); i++) {
			compressedXSeries[i] = xList.get(i);
			compressedYSeries[i] = yList.get(i);
		}

		compressed = true;

		return true;
	}

	/**
	 * Adds the necessary plots.
	 * 
	 * @param xList
	 *            the array in which x coordinate for necessary plot is stored
	 * @param yList
	 *            the array in which y coordinate for necessary plot is stored
	 */
	abstract protected void addNecessaryPlots(ArrayList<Double> xList,
			ArrayList<Double> yList);

	/**
	 * Adds the given coordinate to list.
	 * 
	 * @param xList
	 *            the list to store the X coordinate
	 * @param yList
	 *            the list to store the Y coordinate
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	protected void addToList(ArrayList<Double> xList, ArrayList<Double> yList,
			double x, double y) {
		xList.add(new Double(x));
		yList.add(new Double(y));
	}

	/**
	 * Checks if the given coordinate is in the same grid as previous.
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 * @return true if the given coordinate is in the same grid as previous
	 */
	protected boolean isInSameGridAsPrevious(double x, double y) {
		int xGridIndex;
		int yGridIndex;

		// calculate the X grid index
		if (config.isXLogScale()) {
			double lower = Math.log10(config.getXLowerValue());
			double upper = Math.log10(config.getXUpperValue());
			xGridIndex = (int) ((Math.log10(x) - lower) / (upper - lower) * config
					.getWidthInPixel());
		} else {
			xGridIndex = (int) ((x - config.getXLowerValue())
					/ (config.getXUpperValue() - config.getXLowerValue()) * config
					.getWidthInPixel());
		}

		// calculate the Y grid index
		if (config.isYLogScale()) {
			double lower = Math.log10(config.getYLowerValue());
			double upper = Math.log10(config.getYUpperValue());
			yGridIndex = (int) ((Math.log10(y) - lower) / (upper - lower) * config
					.getHeightInPixel());
		} else {
			yGridIndex = (int) ((y - config.getYLowerValue())
					/ (config.getYUpperValue() - config.getYLowerValue()) * config
					.getHeightInPixel());
		}

		// check if the grid index is the same as previous
		boolean isInSameGridAsPrevious = (xGridIndex == previousXGridIndex && yGridIndex == previousYGridIndex);

		// store the previous grid index
		previousXGridIndex = xGridIndex;
		previousYGridIndex = yGridIndex;

		return isInSameGridAsPrevious;
	}

	/**
	 * Checks if the given value is in range.
	 * 
	 * @param x
	 *            the X value
	 * @return true if the given X value is in range.
	 */
	protected boolean isInXRange(double x) {
		return (x >= config.getXLowerValue() && x <= config.getXUpperValue());
	}

	/**
	 * Checks if the given value is in range.
	 * 
	 * @param y
	 *            the Y value
	 * @return true if the given Y value is in range.
	 */
	protected boolean isInYRange(double y) {
		return (y >= config.getYLowerValue() && y <= config.getYUpperValue());
	}
}
