package org.swtchart.internal.compress;

import java.util.ArrayList;

/**
 * A compressor for line series data.
 */
public class CompressLineSeries extends Compress {

	enum STATE {
		/** stepping over x range */
		SteppingOverXRange,

		/** stepping over y range */
		SteppingOverYRange,

		/** out of range again */
		OutOfRangeAgain,

		/** stepping out of x range */
		SteppingOutOfXRange,

		/** stepping in x range */
		SteppingInXRange,

		/** stepping out of y range */
		SteppingOutOfYRange,

		/** stepping out of range */
		SteppingOutOfRange,

		/** in range again */
		InRangeAgain,

		/** stepping in range */
		SteppingInRange;
	}

	/** the flag indicating whether the previous point is out of range */
	private boolean isPrevOutOfRange;

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

		isPrevOutOfRange = true;

		for (int i = 0; i < xSeries.length; i++) {
			STATE state = getState(i);

			switch (state) {
			case SteppingOutOfYRange:
				addToList(xList, yList, xSeries[i], ySeries[i]);
				break;
			case SteppingOverYRange:
			case SteppingInRange:
			case SteppingInXRange:
				addToList(xList, yList, xSeries[i - 1], ySeries[i - 1]);
				addToList(xList, yList, xSeries[i], ySeries[i]);
				break;
			case SteppingOverXRange:
			case SteppingOutOfXRange:
				addToList(xList, yList, xSeries[i - 1], ySeries[i - 1]);
				addToList(xList, yList, xSeries[i], ySeries[i]);
				i = xSeries.length;
				break;
			case SteppingOutOfRange:
				addToList(xList, yList, xSeries[i], ySeries[i]);
				i = xSeries.length;
				break;
			case InRangeAgain:
				if (!isInSameGridAsPrevious(xSeries[i], ySeries[i])) {
					addToList(xList, yList, xSeries[i], ySeries[i]);
				}
				break;
			case OutOfRangeAgain:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Gets the state for each plot.
	 * 
	 * @param index
	 *            the index for plot
	 * @return the state of plot for the given index
	 */
	private STATE getState(int index) {

		STATE state;

		if (isInXRange(xSeries[index])) {
			if (isInYRange(ySeries[index])) {
				if (index > 0 && isPrevOutOfRange) {
					state = STATE.SteppingInRange;
				} else {
					state = STATE.InRangeAgain;
				}
			} else {
				if (isPrevOutOfRange) {
					if (index > 0
							&& ySeries[index - 1] < config.getYLowerValue()
							&& ySeries[index] > config.getYUpperValue()) {
						state = STATE.SteppingOverYRange;
					} else if (index > 0
							&& xSeries[index - 1] < config.getXLowerValue()
							&& xSeries[index] > config.getXLowerValue()) {
						state = STATE.SteppingInXRange;
					} else {
						state = STATE.OutOfRangeAgain;
					}
				} else {
					state = STATE.SteppingOutOfYRange;
				}
			}
		} else {
			if (!isPrevOutOfRange) {
				state = STATE.SteppingOutOfRange;
			} else if (index > 0
					&& xSeries[index - 1] < config.getXUpperValue()
					&& xSeries[index] > config.getXUpperValue()) {
				state = STATE.SteppingOutOfXRange;
			} else if (index > 0
					&& xSeries[index - 1] < config.getXLowerValue()
					&& xSeries[index] > config.getXUpperValue()) {
				state = STATE.SteppingOverXRange;
			} else {
				state = STATE.OutOfRangeAgain;
			}
		}

		// set flag
		if (isInXRange(xSeries[index])
				&& isInYRange(ySeries[index])) {
			isPrevOutOfRange = false;
		} else {
			isPrevOutOfRange = true;
		}

		return state;
	}
}
