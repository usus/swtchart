package org.swtchart.internal.compress;

import java.util.ArrayList;

/**
 * A compressor for scatter series data
 */
public class CompressScatterSeries extends Compress {

	/** the state indicating if line is visible */
	private boolean isLineVisible;

	/** flag indicating whether the grid is occupied */
	private boolean occupied[][];

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.swtchart.internal.compress.Compress#addNecessaryPlots(java.util.ArrayList
	 * , java.util.ArrayList)
	 */
	@Override
	protected void addNecessaryPlots(ArrayList<Double> xList,
			ArrayList<Double> yList) {

		if (isLineVisible) {
			for (int i = 0; i < xSeries.length; i++) {
				if (!isInSameGridAsPrevious(xSeries[i], ySeries[i])) {
					addToList(xList, yList, xSeries[i], ySeries[i]);
				}
			}
		} else {
			int width = (int) config.getWidthInPixel();
			int height = (int) config.getHeightInPixel();

			if (width <= 0 || height <= 0) {
				return;
			}

			// initialize flag
			occupied = new boolean[width][height];

			for (int i = 0; i < xSeries.length; i++) {
				if (isInXRange(xSeries[i])
						&& isInYRange(ySeries[i])
						&& !isOccupied(xSeries[i], ySeries[i])) {
					addToList(xList, yList, xSeries[i], ySeries[i]);
				}
			}
		}
	}

	/**
	 * check if the grid is already occupied
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 * @return true if the grid is already occupied
	 */
	private boolean isOccupied(double x, double y) {
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

		boolean isOccupied = occupied[xGridIndex][yGridIndex];

		occupied[xGridIndex][yGridIndex] = true;

		return isOccupied;
	}

	/**
	 * Sets the state indicating if the line is visible.
	 * 
	 * @param visible
	 *            the state indicating if the line is visible
	 */
	public void setLineVisible(boolean visible) {
		isLineVisible = visible;
	}
}
