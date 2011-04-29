/**
 *
 */
package org.swtchart.dataset.xy;

/**
 * @author nbraun
 *
 */
public interface IXYDataset {
	/**
	 * Add an x-y point to a data set
	 * @param x coordinate value of x
	 * @param y coordinate value of y
	 */
	void add(double x, double y);

	/**
	 * Clear all values in the current data set
	 */
	void clear();

	/**
	 * Returns an array of X-values of this data set.
	 * @return Array of values for the x-axis
	 */
	double[] getXvalues();

	/**
	 * Returns an array of Y-values of this data set.
	 * @return Array of values for the y-axis
	 */
	double[] getYvalues();

	/**
	 * Get the number of elements in this data set.
	 * @return
	 */
	int size();
}
