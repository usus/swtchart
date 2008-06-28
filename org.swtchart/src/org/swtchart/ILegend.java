package org.swtchart;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * A legend for chart.
 */
public interface ILegend {

	/**
	 * Sets legend visible.
	 * 
	 * @param visible
	 *            the visibility state
	 */
	void setVisible(boolean visible);

	/**
	 * Gets the visibility state.
	 * 
	 * @return true if legend is visible
	 */
	boolean isVisible();

	/**
	 * Sets the background color of legend.
	 * 
	 * @param color
	 *            the background color
	 */
	void setBackground(Color color);

	/**
	 * Gets the background color of legend.
	 * 
	 * @return background color of legend.
	 */
	Color getBackground();

	/**
	 * Sets the foreground color of legend.
	 * 
	 * @param color
	 *            the foreground color
	 */
	void setForeground(Color color);

	/**
	 * Gets the foreground color of legend.
	 * 
	 * @return foreground color of legend.
	 */
	Color getForeground();

	/**
	 * Gets the font.
	 * 
	 * @return the font
	 */
	Font getFont();

	/**
	 * Sets the font.
	 * 
	 * @param font
	 *            the font
	 */
	void setFont(Font font);
}