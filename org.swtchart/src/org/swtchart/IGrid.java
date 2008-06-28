package org.swtchart;

import org.eclipse.swt.graphics.Color;

/**
 * A grid.
 */
public interface IGrid {

	/**
	 * Gets the foreground color.
	 * 
	 * @return the foreground color
	 */
	Color getForeground();

	/**
	 * Sets the foreground color.
	 * 
	 * @param color
	 *            the foreground color
	 */
	void setForeground(Color color);

	/**
	 * Gets the line style.
	 * 
	 * @return the line style.
	 */
	LineStyle getStyle();

	/**
	 * Sets the line style.
	 * 
	 * @param style
	 *            the line style
	 */
	void setStyle(LineStyle style);
}