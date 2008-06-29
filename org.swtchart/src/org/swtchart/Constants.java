package org.swtchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * Constant values.
 */
public class Constants {

	/** the font for large size */
	final static public Font LARGE_FONT = new Font(Display.getDefault(),
			"Tahoma", 14, SWT.BOLD);

	/** the font for medium size */
	final static public Font MEDIUM_FONT = new Font(Display.getDefault(),
			"Tahoma", 12, SWT.BOLD);

	/** the font for small title */
	final static public Font SMALL_FONT = new Font(Display.getDefault(),
			"Tahoma", 10, SWT.BOLD);

	/** the font for tiny size */
	final static public Font TINY_FONT = new Font(Display.getDefault(),
			"Arial", 9, SWT.NORMAL);

	/** the color for light blue */
	final static public Color LIGHT_BLUE = new Color(Display.getDefault(), 153,
			186, 243);

	/** the color for blue */
	final static public Color BLUE = new Color(Display.getDefault(), 0, 0, 255);

	/** the color for red */
	final static public Color RED = new Color(Display.getDefault(), 255, 0, 0);

	/** the color for white */
	final static public Color WHITE = new Color(Display.getDefault(), 255, 255,
			255);

	/** the color for light gray */
	final static public Color LIGHT_GRAY = new Color(Display.getDefault(), 236,
			233, 216);

	/** the color for gray */
	final static public Color GRAY = new Color(Display.getDefault(), 200, 200,
			200);

	/** the color for dark gray */
	final static public Color DARK_GRAY = new Color(Display.getDefault(), 150, 150,
			150);

	/** the color for light green */
	final static public Color LIGHT_GREEN = new Color(Display.getDefault(), 80,
			240, 180);

	/** the color for black */
	final static public Color BLACK = new Color(Display.getDefault(), 0, 0, 0);

}
