package org.swtchart.internal.compress;

import java.util.ArrayList;

/**
 * A compressor for bar series data.
 */
public class CompressBarSeries extends Compress {

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

		double prevX = xSeries[0];
		double maxY = Double.MIN_VALUE;

		for (int i = 0; i < xSeries.length; i++) {
			if (xSeries[i] >= config.getXLowerValue()) {
				if (isInSameGridXAsPrevious(xSeries[i])) {
					if (maxY < ySeries[i]) {
						maxY = ySeries[i];
					}
				} else {
					if (maxY > Double.MIN_VALUE) {
						addToList(xList, yList, prevX, maxY);
					}
					prevX = xSeries[i];
					maxY = ySeries[i];
				}
			}

			if (xSeries[i] > config.getXUpperValue()) {
				break;
			}
		}
		addToList(xList, yList, prevX, maxY);
	}

	/**
	 * Checks if the given x coordinate is in the same grid as previous.
	 * 
	 * @param x
	 *            the X coordinate
	 * @return true if the given coordinate is in the same grid as previous
	 */
	private boolean isInSameGridXAsPrevious(double x) {
		int xGridIndex = (int) ((x - config.getXLowerValue())
				/ (config.getXUpperValue() - config.getXLowerValue()) * config
				.getWidthInPixel());

		boolean isInSameGridAsPrevious = (xGridIndex == previousXGridIndex);

		previousXGridIndex = xGridIndex;

		return isInSameGridAsPrevious;
	}
}
