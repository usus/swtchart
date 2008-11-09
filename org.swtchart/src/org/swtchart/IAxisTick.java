package org.swtchart;

import java.text.Format;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * An axis tick.
 */
public interface IAxisTick {

	/** the minimum grid step hint */
	public static final double MIN_GRID_STEP_HINT = 16;

	/**
	 * Sets the foreground color of axis tick.
	 * 
	 * @param color
	 *            the foreground color of axis tick
	 */
	public void setForeground(Color color);

	/**
	 * Gets the foreground color of axis tick.
	 * 
	 * @return the foreground color of axis tick
	 */
	public Color getForeground();

	/**
	 * Sets the font for tick labels.
	 * 
	 * @param font
	 *            the font for tick labels
	 */
	public void setFont(Font font);
	
	/**
	 * Gets the font for tick labels.
	 * 
	 * @return the font for tick labels
	 */
	Font getFont();

	/**
	 * Gets the state indicating if tick marks are visible.
	 * 
	 * @return true if tick marks are visible
	 */
	boolean isVisible();

	/**
	 * Sets the state indicating if tick marks are visible.
	 * 
	 * @param isVisible
	 *            true to make the tick marks visible
	 */
	void setVisible(boolean isVisible);

	/**
	 * Gets the tick mark step hint in pixels.
	 * 
	 * @return the tick mark step hint in pixels
	 */
	int getTickMarkStepHint();

	/**
	 * Sets the tick mark step hint in pixels.
	 * 
	 * @param tickMarkStepHint
	 *            the tick mark step hint with pixels (> IAxisTick.MIN_GRID_STEP_HINT)
	 */
	void setTickMarkStepHint(int tickMarkStepHint);

    /**
     * Sets the format for axis tick label. <tt>DecimalFormat</tt> and
     * <tt>DateFormat</tt> should be used for <tt>double[]</tt> series and
     * <tt>Date[]</tt> series respectively.
     * 
     * @param format
     *            the format
     */
    void setFormat(Format format);

    /**
     * Gets the format for axis tick label.
     * 
     * @return the format
     */
    Format getFormat();
}