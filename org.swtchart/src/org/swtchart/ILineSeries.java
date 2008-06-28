package org.swtchart;

import org.eclipse.swt.graphics.Color;

/**
 * Line series.
 */
public interface ILineSeries extends ISeries {

	/**
	 * A plot symbol type.
	 */
	public enum PlotSymbolType {

		/** none */
		NONE("None"),

		/** circle */
		CIRCLE("Circle"),

		/** square */
		SQUARE("Square"),

		/** diamond */
		DIAMOND("Diamond"),

		/** triangle */
		TRIANGLE("Triangle"),

		/** inverted triangle */
		INVERTED_TRIANGLE("Inverted Triangle"),

		/** cross */
		CROSS("Cross"),

		/** plus */
		PLUS("Plus");

		/** the label for plot symbol */
		public final String label;

		/**
		 * Constructor.
		 * 
		 * @param label
		 *            plot symbol label
		 */
		private PlotSymbolType(String label) {
			this.label = label;
		}
	}

	/**
	 * Gets the symbol type.
	 * 
	 * @return the symbol type
	 */
	PlotSymbolType getSymbolType();

	/**
	 * Sets the symbol type. If null is given, default type
	 * <tt>PlotSymbolType.CIRCLE</tt> will be set.
	 * 
	 * @param type
	 *            the symbol type
	 */
	void setSymbolType(PlotSymbolType type);

	/**
	 * Gets the symbol size in pixels.
	 * 
	 * @return the symbol size
	 */
	int getSymbolSize();

	/**
	 * Sets the symbol size in pixels.
	 * 
	 * @param size
	 *            hte symbol size
	 */
	void setSymbolSize(int size);

	/**
	 * Gets the symbol color.
	 * 
	 * @return the symbol color
	 */
	Color getSymbolColor();

	/**
	 * Sets the symbol color. If null is given, default color will be set.
	 * 
	 * @param color
	 *            the symbol color
	 */
	void setSymbolColor(Color color);
	
	/**
	 * Gets line style.
	 * 
	 * @return line style.
	 */
	LineStyle getLineStyle();

	/**
	 * Sets line style. If null is given, default line style will be set.
	 * 
	 * @param style
	 *            line style
	 */
	void setLineStyle(LineStyle style);

	/**
	 * Gets the line color.
	 * 
	 * @return the line color
	 */
	Color getLineColor();

	/**
	 * Sets line color. If null is given, default color will be set.
	 * 
	 * @param color
	 *            the line color
	 */
	void setLineColor(Color color);
}