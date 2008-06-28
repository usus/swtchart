package org.swtchart;

import org.eclipse.swt.graphics.Color;

/**
 * Bar series.
 */
public interface IBarSeries extends ISeries {
	
	/**
	 * Gets the bar padding in percentage.
	 * 
	 * @return the bar padding in percentage
	 */
	int getBarPadding();

	/**
	 * Sets the bar padding in percentage.
	 * 
	 * @param padding
	 *            the bar padding in percentage
	 */
	void setBarPadding(int padding);

	/**
	 * Gets the bar color.
	 * 
	 * @return the bar color
	 */
	Color getBarColor();

	/**
	 * Sets the bar color. If null is given, default color will be set.
	 * 
	 * @param color
	 *            the bar color
	 */
	void setBarColor(Color color);
}